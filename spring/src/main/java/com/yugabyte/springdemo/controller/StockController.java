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

    @GetMapping("/stock")
    public Page<Stock> getStocks(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    @GetMapping("/stock/find/{item_id}")
    public Map<Integer, Integer> getStock(@PathVariable("item_id") Long stockId){
        Map<Integer, Integer> map = new HashMap<>();
        Integer units = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId)).getUnits();
        Integer price = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId)).getPrice();
        map.put(units,price);
        return map;
    }

    @PostMapping("/stock/item/create/{price}")
    public Long createStock(@PathVariable("price") Integer price, @Valid @RequestBody Stock stock) {
        stock.setPrice(price);
        stockRepository.save(stock);
        return stock.getStockId();
    }

    @PostMapping("/stock/add/{item_id}/{number}")
    public Boolean addStock(@PathVariable("item_id") Long stockId, @PathVariable("number") Integer units){
        return stockRepository.findById(stockId)
                .map(stock -> {
                    stock.addUnits(units);
                    stockRepository.save(stock);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId));
    }

    @PostMapping("/stock/subtract/{item_id}/{number}")
    public Boolean subtractStock(@PathVariable("item_id") Long stockId, @PathVariable("number") Integer units){
        return stockRepository.findById(stockId)
                .map(stock -> {
                    if(stock.subtractUnits(units)) {
                        stockRepository.save(stock);
                        return true;
                    } else {
                        try {
                            throw new Exception("error!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId));
    }
}
