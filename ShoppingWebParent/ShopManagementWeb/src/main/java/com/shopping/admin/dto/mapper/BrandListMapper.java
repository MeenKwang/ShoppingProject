package com.shopping.admin.dto.mapper;

import com.shopping.admin.dto.BrandListDto;
import com.shopping.common.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface BrandListMapper {
    BrandListDto brandToBrandListDto(Brand brand);
}
