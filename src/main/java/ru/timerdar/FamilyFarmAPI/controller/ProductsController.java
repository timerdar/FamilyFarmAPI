package ru.timerdar.FamilyFarmAPI.controller;


import org.springframework.web.bind.annotation.*;
import ru.timerdar.FamilyFarmAPI.db.ProductDB;
import ru.timerdar.FamilyFarmAPI.dto.Product;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductDB db = new ProductDB();

    @PostMapping("/add")
    public String addProduct(@RequestBody Product product){
        return db.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts(){
        return db.productList();
    }

    @PatchMapping("/change")
    public Product changePrice(@RequestBody Product product){
        return db.changePrice(product);
    }

    @DeleteMapping("/delete")
    public String deleteProduct(@RequestBody String name){
        return db.delete(name);
    }
}
