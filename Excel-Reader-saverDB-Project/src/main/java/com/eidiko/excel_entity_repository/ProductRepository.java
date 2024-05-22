package com.eidiko.excel_entity_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eidiko.entity.ProductEntity;
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{

}
