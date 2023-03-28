package com.shopping.admin.service.impl;

import com.shopping.admin.dto.BrandListDto;
import com.shopping.admin.dto.mapper.BrandListMapper;
import com.shopping.admin.repository.BrandRepository;
import com.shopping.admin.service.BrandService;
import com.shopping.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandListMapper brandListMapper;
    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, BrandListMapper brandListMapper) {
        this.brandRepository = brandRepository;
        this.brandListMapper = brandListMapper;
    }
    @Override
    public List<BrandListDto> listByPage(Integer pageNum, String sortField, String sortDir, String nameSearch, Integer pageSize) {
        Sort sort = Sort.by(sortField);
        if(sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if(sortDir.equals("desc")) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        List<Brand> brandPage = brandRepository.findAll(pageable, nameSearch);
        List<BrandListDto> brandListDtoPage = brandPage.stream().map(brand -> brandListMapper.brandToBrandListDto(brand)).collect(Collectors.toList());

        return brandListDtoPage;
    }
}
