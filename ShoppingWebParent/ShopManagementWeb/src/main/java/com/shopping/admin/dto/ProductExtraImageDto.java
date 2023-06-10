package com.shopping.admin.dto;

public class ProductExtraImageDto {
    private Integer id;
    private String name;
    private Integer productId;

    private boolean modified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "ProductExtraImageDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", productId=" + productId +
                ", modified=" + modified +
                '}';
    }
}
