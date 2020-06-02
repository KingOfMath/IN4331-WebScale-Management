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
import java.util.Optional;
import java.util.Set;
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

        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserId " + order.getUserId() + " not found")));
        Order newOrder = orderRepository.save(order);

        return newOrder.getOrderId();
    }

    @GetMapping("/orders/find/{order_id}")
    public Order getOrder(@PathVariable("order_id") UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @DeleteMapping("/orders/remove/{order_id}")
    public Boolean deleteOrder(@PathVariable("order_id") UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    orderRepository.delete(order);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @PostMapping("/orders/addItem/{order_id}/{item_id}")
    public Boolean addItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                order.addStock(item);
                                Integer old = order.getOrderTotal();
                                order.setOrderTotal(old + item.getPrice() * item.getUnits());
                                orderRepository.save(order);
                                return true;
                            }).orElseThrow(() -> new ResourceNotFoundException("Item not found with id " + itemId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @DeleteMapping("/orders/removeItem/{order_id}/{item_id}")
    public Boolean removeItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                order.removeStock(item);
								Integer old = order.getOrderTotal();
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
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getUserId();
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
