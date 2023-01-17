package com.lab5.controllers;

import com.lab5.entities.Product;
import com.lab5.entities.Supplier;
import com.lab5.repositoriesInterfaces.ProductRepository;
import com.lab5.repositoriesInterfaces.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class SupplierController {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<Supplier>();
        supplierRepository.findAll().forEach(suppliers::add);

        if (suppliers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @GetMapping("/suppliers/{id}/products")
    public ResponseEntity<List<Product>> getProductsBySupplierId(@PathVariable("id") Long id) {
        List<Product> products = productRepository.getProductsBySupplierId(id);

        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }
}
