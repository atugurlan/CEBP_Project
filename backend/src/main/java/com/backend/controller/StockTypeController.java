package com.backend.controller;

import com.backend.entity.StockType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class StockTypeController {

    @GetMapping("/stock-types")
    public List<StockType> getAllStockTypes() {
        return Arrays.asList(StockType.values());
    }
}
