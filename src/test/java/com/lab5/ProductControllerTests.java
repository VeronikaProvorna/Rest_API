package com.lab5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab5.dtos.ProductCreateDto;
import com.lab5.repositoriesInterfaces.ProductRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
@Sql({"classpath:test-data.sql"})
public class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    //test for get all products endpoint "/products"
    @Test
    public void getAllProductsListTest() throws Exception {
        this.mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[2].id").value(3))
                .andExpect(jsonPath("$.[3].id").value(4));

    }

    @Test
    public void getProductByExistedIdTest() throws Exception {
        this.mockMvc.perform(get("/products/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType((APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Home printer"))
                .andExpect(jsonPath("$.price").value(180))
                .andExpect(jsonPath("$.category").value("printer"));

    }

    @Test
    public void getProductByIncorrectIdTest() throws Exception {
        this.mockMvc.perform(get("/products/{id}", 6))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProductByCategoryAndPriceLessThanGivenTest() throws Exception {
        this.mockMvc.perform(get("/products/category/{category}/price/{price}", "laptop", 1500))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType((APPLICATION_JSON)))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].name").value("Spectre x360"))
                .andExpect(jsonPath("$.[0].price").value(1400))
                .andExpect(jsonPath("$.[0].category").value("laptop"));
    }

    @Test
    public void getProductByNotExistCategoryAndPriceLessThanGivenTest() throws Exception {
        this.mockMvc.perform(get("/products/category/{category}/price/{price}", "keyboard", 1500))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteProductByIdTest() throws Exception {
        this.mockMvc.perform(delete("/products/{id}", 4))
                .andDo(print())
                .andExpect(status().isOk());

        //check if this product was deleted
        Assertions.assertEquals(3, productRepository.findAll().size());
    }

    @Test
    public void updateProductByIdTest() throws Exception {
        //change the price for 1 product
        ProductCreateDto productCreateDto = new ProductCreateDto("Home printer",
                200, "printer", 1L);

        String jsonProductToUpdate = objectMapper.writeValueAsString(productCreateDto);

        this.mockMvc.perform(put("/products/{id}", 1)
                .contentType(APPLICATION_JSON)
                .content(jsonProductToUpdate))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Home printer"))
                .andExpect(jsonPath("$.price").value(200))
                .andExpect(jsonPath("$.category").value("printer"));
    }

    @Test
    public void updateProductByIdAndIncorrectSupplierIdTest() throws Exception {
        //change supplier Id to not existed
        ProductCreateDto productCreateDto = new ProductCreateDto("Home printer",
                200, "printer", 7L);

        String jsonProductToUpdate = objectMapper.writeValueAsString(productCreateDto);

        this.mockMvc.perform(put("/products/{id}", 1)
                .contentType(APPLICATION_JSON)
                .content(jsonProductToUpdate))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProductByIncorrectIdTest() throws Exception {
        ProductCreateDto productCreateDto = new ProductCreateDto("Home printer",
                200, "printer", 7L);

        String jsonProductToUpdate = objectMapper.writeValueAsString(productCreateDto);

        this.mockMvc.perform(put("/products/{id}", 8)
                .contentType(APPLICATION_JSON)
                .content(jsonProductToUpdate))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void createProductTest() throws Exception {
        ProductCreateDto productCreateDto = new ProductCreateDto("New product",
                2000, "printer", 1L);

        String jsonProductToUpdate = objectMapper.writeValueAsString(productCreateDto);

        this.mockMvc.perform(post("/products")
                .contentType(APPLICATION_JSON)
                .content(jsonProductToUpdate))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("New product"))
                .andExpect(jsonPath("$.price").value(2000))
                .andExpect(jsonPath("$.category").value("printer"));

        //check if new product was added
        Assertions.assertEquals(5, productRepository.findAll().size());
    }

    @Test
    public void createProductWithIncorrectSupplierIdTest() throws Exception {
        ProductCreateDto productCreateDto = new ProductCreateDto("New product",
                2000, "printer", 8L);

        String jsonProductToUpdate = objectMapper.writeValueAsString(productCreateDto);

        this.mockMvc.perform(post("/products")
                .contentType(APPLICATION_JSON)
                .content(jsonProductToUpdate))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}