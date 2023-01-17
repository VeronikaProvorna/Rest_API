package com.lab5.repositoriesInterfaces;

import com.lab5.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Supplier s JOIN s.products p WHERE s.id=(:sId)")
    List<Product> getProductsBySupplierId(@Param("sId") Long id);

    List<Product> findByCategoryAndPriceLessThan(String category, int price);
}
