package com.shopping.admin.dto;

import com.shopping.common.entity.Category;

import java.util.Set;

public class BrandListDto {
    private Integer id;
    private String name;
    private String logo;
    private Set<Category> categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}
