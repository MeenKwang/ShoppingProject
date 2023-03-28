package com.shopping.admin.service;

import com.shopping.admin.dto.BrandListDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BrandService {
    public List<BrandListDto> listByPage(Integer pageNum, String sortField, String sortDir, String nameSearch, Integer pageSize);
}
