package com.shopping.admin.repository;

import com.shopping.common.entity.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    @Query("SELECT b FROM Brand b WHERE b.name = ?2")
    public List<Brand> findAll(Pageable pageable, String name);
    public Optional<Brand> findById(Integer id);
}
