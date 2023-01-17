package com.lab5.controllers;

import com.lab5.dtos.ProductCreateDto;
import com.lab5.entities.Product;
import com.lab5.entities.Supplier;
import com.lab5.repositoriesInterfaces.ProductRepository;
import com.lab5.repositoriesInterfaces.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach(products::add);

        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //get all products by category name and by price lower than price in parameters
    @GetMapping("/products/category/{category}/price/{price}")
    public ResponseEntity<List<Product>> getProductsByCategoryAndPrice(@PathVariable("category") String category,
                                                                       @PathVariable("price") int price) {
        List<Product> products = productRepository.findByCategoryAndPriceLessThan(category, price);

        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody ProductCreateDto productCreateDto) {
        //get supplier by id from received product data
        Optional<Supplier> supplier = supplierRepository.findById(productCreateDto.getSupplierId());

        //if supplier exist - create new product
        if (supplier.isPresent()) {
            Product newProduct = new Product(productCreateDto.getName(),
                    productCreateDto.getPrice(),
                    supplier.get(),
                    productCreateDto.getCategory());

            Product responseProduct = productRepository.save(newProduct);
            return new ResponseEntity<>(responseProduct, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductCreateDto productCreateDto,
                                                 @PathVariable("id") Long id) {
        //check if product with requested id exist
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //get supplier by id from received product data
        Optional<Supplier> supplier = supplierRepository.findById(productCreateDto.getSupplierId());

        //if supplier exist - update product
        if (supplier.isPresent()) {
            //update product found by id and return id
            product.map(p -> {
                p.setSupplier(supplier.get());
                p.setCategory(productCreateDto.getCategory());
                p.setName(productCreateDto.getName());
                p.setPrice(productCreateDto.getPrice());
                return productRepository.save(p);
            });

            Product responseProduct = product.get();
            return new ResponseEntity<>(responseProduct, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productRepository.deleteById(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
