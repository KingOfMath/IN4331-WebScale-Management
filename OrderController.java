package com.yugabyte.springdemo.controller;

import com.yugabyte.springdemo.exception.ResourceNotFoundException;
import com.yugabyte.springdemo.model.*;
import com.yugabyte.springdemo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/orders")
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @PostMapping("/orders/create/{user_id}")
    public UUID createOrder(@Valid @RequestBody Order order, @PathVariable("user_id") Long userId) {

        Integer orderTotal = 0;
        for (Stock orderStock : order.getStocks()) {
            orderTotal += stockRepository.findById(orderStock.getStockId())
                    .map(stock -> {
                        return stock.getPrice() * stock.getUnits();
                    }).orElseThrow(() -> new ResourceNotFoundException("Stock not found with Id: " + orderStock.getStockId()));
        }
        order.setOrderTotal(orderTotal);
        //order.setUserId(userId);

        order.setPaid(false);

        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserId " + order.getUserId() + " not found")));
        Order newOrder = orderRepository.save(order);

        return newOrder.getOrderId();
    }

    @DeleteMapping("/orders/remove/{order_id}")
    public Boolean deleteOrder(@PathVariable("order_id") UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    orderRepository.delete(order);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @PostMapping("/orders/addItem/{order_id}/{item_id}/{amount}")
    public Boolean addItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId, @PathVariable("amount") Integer amount) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                Integer oldUnits = item.getUnits();
                                if (oldUnits - amount >= 0) {
                                    item.setUnits(oldUnits - amount);
                                    stockRepository.save(item);
                                } else
                                    return false;

                                item.setUnits(amount);
                                boolean flag = false;
                                Integer old = order.getOrderTotal();
                                for (Stock orderStock : order.getStocks()) {
                                    if (orderStock.getStockId().equals(itemId)) {
                                        Integer temp = orderStock.getUnits();
                                        orderStock.setUnits(temp + amount);
                                        flag = true;
                                    }
                                }
                                if (!flag)
                                    order.addStock(item);

                                order.setOrderTotal(old + item.getPrice() * item.getUnits());
                                orderRepository.save(order);
                                return true;
                            }).orElseThrow(() -> new ResourceNotFoundException("Item not found with id " + itemId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @DeleteMapping("/orders/removeItem/{order_id}/{item_id}/{amount}")
    public Boolean removeItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId, @PathVariable("amount") Integer amount) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                Integer oldUnits = item.getUnits();
                                Integer old = order.getOrderTotal();
                                for (Stock orderStock : order.getStocks()) {
                                    if (orderStock.getStockId().equals(itemId)) {
                                        Integer temp = orderStock.getUnits();
                                        if (temp - amount >= 0)
                                            orderStock.setUnits(temp - amount);
                                        else
                                            return false;
                                    }
                                }
                                item.setUnits(oldUnits + amount);
                                stockRepository.save(item);

                                item.setUnits(amount);

                                order.setOrderTotal(old - item.getPrice() * item.getUnits());
                                orderRepository.save(order);
                                return true;
                            }).orElseThrow(() -> new ResourceNotFoundException("Item not found with id " + itemId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @GetMapping("/orders/find/{order_id}")
    public String findOrder(@PathVariable("order_id") UUID orderId) {
        Boolean paid = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getPaid();
        Long userId = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getUser().getUserId();
        Integer totalCost = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getOrderTotal();
        String stockId = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getStockId();
        return "order_id:" + orderId + ", paid:" + paid + ", stock_id:" + stockId + ", user_id:" + userId + ", totalCost:" + totalCost;

    }

    @PostMapping("/orders/checkout/{order_id}")
    public Boolean checkout(@PathVariable("order_id") UUID orderId) throws IOException {

        Integer totalCost = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getOrderTotal();
        Long userId = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getUserId();

        String cmd = "curl http://localhost:8080/payment/pay/"
                + userId + "/" + orderId + "/" + totalCost;
        Process process = Runtime.getRuntime().exec(cmd);
        int exitVal = process.exitValue();
        return exitVal == 0;
    }
}
