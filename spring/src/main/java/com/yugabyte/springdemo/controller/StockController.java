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
    public Map<String, Integer> getStock(@PathVariable("item_id") Long stockId){
        Integer units = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId)).getUnits();
        Integer price = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId)).getPrice();

        Map<String, Integer> map = new HashMap<>();
        map.put("stock", units);
        map.put("price", price);
        return map;
    }

    @PostMapping("/stock/item/create/{price}")
    public Map<String, Long> createStock(@PathVariable("price") Integer price, @Valid @RequestBody Stock stock) {
        stock.setPrice(price);
        stock.setUnits(0);
        stockRepository.save(stock);
        Map<String, Long> map = new HashMap<>();
        map.put("item_id", stock.getStockId());
        return map;
    }

    @PostMapping("/stock/add/{item_id}/{number}")
    public void addStock(@PathVariable("item_id") Long stockId, @PathVariable("number") Integer units){
        stockRepository.findById(stockId)
                .map(stock -> {
                    stock.addUnits(units);
                    stockRepository.save(stock);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId));
    }

    @PostMapping("/stock/subtract/{item_id}/{number}")
    public void subtractStock(@PathVariable("item_id") Long stockId, @PathVariable("number") Integer units){
        stockRepository.findById(stockId)
                .map(stock -> {
                    if(stock.subtractUnits(units)) {
                        stockRepository.save(stock);
                        return true;
                    } else {
                        throw new ResourceNotFoundException("Insufficient amount of stock available for item " + stockId);
                    }
                }).orElseThrow(() -> new ResourceNotFoundException("stock not found with id " + stockId));
    }
}
