package ru.timerdar.FamilyFarmAPI.controller;


import org.springframework.web.bind.annotation.*;
import ru.timerdar.FamilyFarmAPI.db.ProductDB;
import ru.timerdar.FamilyFarmAPI.dto.Product;
import ru.timerdar.FamilyFarmAPI.dto.TextResponse;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductDB db = new ProductDB();

    @PostMapping("/add")
    public TextResponse addProduct(@RequestBody Product product){
        return new TextResponse(db.addProduct(product));
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts(){
        return db.productList();
    }

    @PatchMapping("/change")
    public Product changePrice(@RequestBody Product product){
        return db.changePrice(product);
    }

    @DeleteMapping("/delete/{product}")
    public TextResponse deleteProduct(@PathVariable String product){
        return new TextResponse(db.delete(product));
    }
}
