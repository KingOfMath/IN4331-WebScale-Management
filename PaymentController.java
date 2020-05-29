package com.yugabyte.springdemo.controller;


import com.yugabyte.springdemo.exception.ResourceNotFoundException;
import com.yugabyte.springdemo.model.Order;
import com.yugabyte.springdemo.model.Payment;
import com.yugabyte.springdemo.repository.OrderRepository;
import com.yugabyte.springdemo.repository.PaymentRepository;
import com.yugabyte.springdemo.repository.UserRepository;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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

    @PostMapping("/payment/pay/{user_id}/{order_id}/{amount}")
    public Boolean pay(@PathVariable("user_id") Long userId, @PathVariable("order_id") UUID orderId, @PathVariable("amount") Integer amount) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    return userRepository.findById(userId)
                            .map(user -> {
                                if (!order.getPaid()) {
                                    user.subtract(amount);
                                    userRepository.save(user);
                                    return true;
                                }
                                return false;
                            }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @PostMapping("/payment/cancel/{user_id}/{order_id}")
    public Boolean cancel(@PathVariable("user_id") Long userId, @PathVariable("order_id") UUID orderId){
        return orderRepository.findById(orderId)
                .map(order -> {
                    return userRepository.findById(userId)
                            .map(user -> {
                                if (order.getPaid()) {
                                    user.add(order.getOrderTotal());
                                    userRepository.save(user);
                                    return true;
                                }
                                return false;
                            }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @GetMapping("/payment/status/{order_id}")
    public Boolean getStatus(@PathVariable("order_id") UUID orderId){
        return orderRepository.findById(orderId)
                .map(Order::getPaid).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }
}
