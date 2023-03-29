package com.shopping.admin.repository;

import com.shopping.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(Pageable pageable, String name);

    Optional<Brand> findById(Integer id);
}
