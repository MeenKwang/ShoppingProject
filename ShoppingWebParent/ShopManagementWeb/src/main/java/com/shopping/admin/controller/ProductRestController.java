package com.shopping.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.admin.dto.ProductFormDto;
import com.shopping.admin.dto.ProductListDto;
import com.shopping.admin.dto.mapper.ProductFormMapper;
import com.shopping.admin.helper.ProductSaveHelper;
import com.shopping.admin.service.ProductService;
import com.shopping.admin.util.FileUploadUtil;
import com.shopping.common.entity.Product;
import com.shopping.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    private final ProductService productService;
    private final ProductFormMapper productFormMapper;

    @Autowired
    public ProductRestController(ProductService productService, ProductFormMapper productFormMapper) {
        this.productService = productService;
        this.productFormMapper = productFormMapper;
    }

    @GetMapping("products")
    public ResponseEntity<Page<ProductListDto>> listFirstPage() {
        return listByPage(1, 0, 0, "", 15, "name", "asc");
    }

    @GetMapping("products/page/{pageNum}")
    public ResponseEntity<Page<ProductListDto>> listByPage(@PathVariable("pageNum") int pageNum, @RequestParam("categoryId") Integer categoryId,
                                                           @RequestParam("brandId") Integer brandId, @RequestParam("nameSearch") String nameSearch,
                                                           @RequestParam("pageSize") Integer pageSize, @RequestParam("sortField") String sortField,
                                                           @RequestParam("sortDir") String sortDir) {
        Page<ProductListDto> productListDtoPage = productService.listByPage(pageNum, categoryId, brandId, nameSearch, pageSize, sortField, sortDir);
        return ResponseEntity.ok(productListDtoPage);
    }
    @PostMapping("products/save")
    public ResponseEntity<ProductFormDto> save(@RequestParam("productForm") String productForm,
                                               @RequestParam(name = "mainImageFile", required = false) MultipartFile mainImageFile,
                                               @RequestParam(name = "extraImages", required = false) MultipartFile[] extraImages) throws JsonProcessingException {
        if(mainImageFile != null) System.out.println(mainImageFile.getOriginalFilename());
        ProductFormDto productFormDto = new ObjectMapper().readValue(productForm, ProductFormDto.class);
        try {
            Product product = productFormMapper.productFormDtoToProduct(productFormDto);
            if(product.getId() == null) {
                ProductSaveHelper.setMainImageName(mainImageFile, product);
                //save product detail
                productService.save(product);
            } else {

            }
//            if (productFormDto.getMainImageFile() != null) {
//                String fileName = StringUtils.cleanPath(Objects.requireNonNull(productFormDto.getMainImageFile().getOriginalFilename()));
//                product.setMainImage(fileName);
//            }
//            Product savedProduct = productService.save(product);
//            if (productFormDto.getMainImageFile() != null) {
//                String fileName = StringUtils.cleanPath(Objects.requireNonNull(productFormDto.getMainImageFile().getOriginalFilename()));
//                String uploadDir = "../product-images/" + savedProduct.getId();
//                FileUploadUtil.cleanDir(uploadDir);
//                FileUploadUtil.saveFile(uploadDir, fileName, productFormDto.getMainImageFile());
//            }
            return ResponseEntity.ok(productFormDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

//    @GetMapping("/products/{id}/enabled/{status}")
//    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
//                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
//        productService.updateProductEnabledStatus(id, enabled);
//        String status = enabled ? "enabled" : "disabled";
//        String message = "The Product ID " + id + " has been " + status;
//        redirectAttributes.addFlashAttribute("message", message);
//
//        return "redirect:/products";
//    }

    @GetMapping("/products/delete/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "id") Integer id) {
        try {
            productService.deleteProduct(id);
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;

            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

        } catch (ProductNotFoundException ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/products/get/{id}")
    public ResponseEntity<ProductFormDto> getProductById(@PathVariable("id") Integer id) {
        try {
            ProductFormDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (ProductNotFoundException e) {
            System.out.println("Product not found");
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
