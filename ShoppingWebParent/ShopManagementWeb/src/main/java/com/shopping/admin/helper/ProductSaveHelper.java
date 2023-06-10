package com.shopping.admin.helper;

import com.shopping.admin.dto.ProductExtraImageDto;
import com.shopping.admin.dto.ProductFormDto;
import com.shopping.admin.repository.ProductImageRepository;
import com.shopping.admin.util.FileUploadUtil;
import com.shopping.common.entity.Product;
import com.shopping.common.entity.ProductImage;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class ProductSaveHelper {

    private static ProductImageRepository productImageRepository;

    public ProductSaveHelper(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    public static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    public static void saveMainImageFile(MultipartFile mainImage, Product savedProduct) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }
    }

    public static void deleteExtraImagesWereRemovedOnForm(Product product) throws IOException {
        Set<ProductImage> images = productImageRepository.findAllByProductId(product.getId());
        for(ProductImage image : images) {
            if(!isInNewList(image, product.getImages())) {
                String extraImageDir = "../product-images/" + product.getId() + "/extras/" + image.getName();
                Path dirPath = Paths.get(extraImageDir);
                Files.delete(dirPath);
            }
        }
    }

    private static boolean isInNewList(ProductImage oldImage, Set<ProductImage> newImageList) {
        for(ProductImage newImage: newImageList) {
            if(oldImage.getId() == newImage.getId() && oldImage.getName() == newImage.getName()) return true;
        }
        return false;
    }

    public static void saveExtraImages(MultipartFile[] extraImages, Integer savedProductId) throws IOException {
        for(MultipartFile image : extraImages) {
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            String uploadDir = "/product-images/" + savedProductId + "/extras";
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        }
    }

    public static void setExtraImages(Set<ProductImage> images) {
        Integer i = 1;
        for(ProductImage image : images) {
            image.setName(i.toString());
            i++;
        }
    }

}
