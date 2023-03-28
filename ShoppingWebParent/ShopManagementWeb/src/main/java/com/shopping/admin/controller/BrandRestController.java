package com.shopping.admin.controller;

import com.shopping.admin.dto.BrandListDto;
import com.shopping.admin.dto.CategorySelect;
import com.shopping.admin.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BrandRestController {
    private final BrandService brandService;

    @Autowired
    public BrandRestController(BrandService brandService) {
        this.brandService = brandService;
    }
    @GetMapping("brands")
    public ResponseEntity<List<BrandListDto>> getFirstPage() {
        return getFirstPage(1, "name", "asc", null, 10);
    }
    @GetMapping("brands/page/{pageNum}")
    public ResponseEntity<List<BrandListDto>> getFirstPage(@PathVariable("pageNum") Integer pageNum, @RequestParam("sortField") String sortField,
                                                           @RequestParam("sortDir") String sortDir, @RequestParam(value = "nameSearch", required = false) String nameSearch,
                                                           @RequestParam("pageSize") int pageSize) {
        List<BrandListDto> list = brandService.listByPage(pageNum, sortField, sortDir, nameSearch, pageSize);
        return ResponseEntity.ok(list);
    }
}
