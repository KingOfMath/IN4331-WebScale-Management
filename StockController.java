package com.yugabyte.springdemo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.yugabyte.springdemo.exception.ResourceNotFoundException;
import com.yugabyte.springdemo.model.Stock;
import com.yugabyte.springdemo.repository.StockRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/stocks")
    public Page<Stock> getStocks(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }


    @GetMapping("/stock/find/{item_id}")
    public Map<Integer, Double> getStock(@PathVariable Long stockId){
        Map<Integer, Double> map = new HashMap<>();
        Integer units = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId)).getUnits();
        Double price = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId)).getPrice();
        map.put(units,price);
        return map;
    }

    @PostMapping("/stock/item/create/{price}")
    public Long createStock(@PathVariable double price, @Valid @RequestBody Stock stock) {
        stock.setPrice(price);
        stockRepository.save(stock);
        return stock.getStockId();
    }

    @PostMapping("/stock/add/{item_id}/{number}")
    public Boolean addStock(@PathVariable Long stockId, @PathVariable Integer units){
        return stockRepository.findById(stockId)
                .map(stock -> {
                    stock.addUnits(units);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId));
    }

    @PostMapping("/stock/subtract/{item_id}/{number}")
    public Boolean subtractStock(@PathVariable Long stockId, @PathVariable Integer units){
        return stockRepository.findById(stockId)
                .map(stock -> {
                    stock.subtractUnits(units);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId));
    }
}
