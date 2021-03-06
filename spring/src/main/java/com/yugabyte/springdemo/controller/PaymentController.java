package com.yugabyte.springdemo.controller;


import com.yugabyte.springdemo.exception.ResourceNotFoundException;
import com.yugabyte.springdemo.model.Order;
import com.yugabyte.springdemo.model.Payment;
import com.yugabyte.springdemo.repository.OrderRepository;
import com.yugabyte.springdemo.repository.PaymentRepository;
import com.yugabyte.springdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/payment")
    public Page<Payment> getPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    @PostMapping("/payment/pay/{user_id}/{order_id}")
    public void pay(@PathVariable("user_id") Long userId, @PathVariable("order_id") UUID orderId) {
        orderRepository.findById(orderId)
                .map(order -> {
                    return userRepository.findById(userId)
                            .map(user -> {
                                if (!order.getPaid()) {
                                    user.subtract(order.getOrderTotal());
                                    userRepository.save(user);
                                    order.setPaid(true);
                                    orderRepository.save(order);
                                    return true;
                                }
                                return false;
                            }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @PostMapping("/payment/cancel/{user_id}/{order_id}")
    public void cancel(@PathVariable("user_id") Long userId, @PathVariable("order_id") UUID orderId){
        orderRepository.findById(orderId)
                .map(order -> {
                    return userRepository.findById(userId)
                            .map(user -> {
                                if (order.getPaid()) {
                                    user.add(order.getOrderTotal());
                                    userRepository.save(user);
                                    order.setPaid(false);
                                    orderRepository.save(order);
                                    return true;
                                }
                                return false;
                            }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @GetMapping("/payment/status/{order_id}")
    public Map<String, Boolean> getStatus(@PathVariable("order_id") UUID orderId){
        boolean paid = orderRepository.findById(orderId)
                .map(Order::getPaid).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        Map<String, Boolean> map = new HashMap<>();
        map.put("paid", paid);
        return map;
    }
}
