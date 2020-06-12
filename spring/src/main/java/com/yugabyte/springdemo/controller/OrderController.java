package com.yugabyte.springdemo.controller;

import com.yugabyte.springdemo.exception.ResourceNotFoundException;
import com.yugabyte.springdemo.model.Order;
import com.yugabyte.springdemo.model.User;
import com.yugabyte.springdemo.repository.OrderRepository;
import com.yugabyte.springdemo.repository.StockRepository;
import com.yugabyte.springdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

//    @Autowired
//    private CartRepository cartRepository;

    @GetMapping("/orders")
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @PostMapping("/orders/create/{user_id}")
    public Map<String, UUID> createOrder(@Valid @RequestBody Order order, @PathVariable("user_id") Long userId) {

        Integer orderTotal = 0;
//        for (Order.Cart cart : order.getCarts()) {
//            total_cost += cart.getPrice() * cart.getUnits();
//        }
        order.setOrderTotal(orderTotal);
        //order.setUserId(userId);

        order.setPaid(false);

        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserId " + order.getUser().getUserId() + " not found")));
        Order newOrder = orderRepository.save(order);

        Map<String, UUID> map = new HashMap<>();
        map.put("order_id", newOrder.getOrderId());

        return map;
    }

    @DeleteMapping("/orders/remove/{order_id}")
    public void deleteOrder(@PathVariable("order_id") UUID orderId) {
        orderRepository.findById(orderId)
                .map(order -> {
                    orderRepository.delete(order);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @PostMapping("/orders/addItem/{order_id}/{item_id}")
    public void addItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId) {
        int amount = 1;
        orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                Integer oldUnits = item.getUnits();
                                if (oldUnits - amount >= 0) {
                                    item.setUnits(oldUnits - amount);
                                    stockRepository.save(item);
                                } else
                                    return false;


                                if (order.cartMap.containsKey(itemId)) {
                                    order.cartMap.put(itemId, order.cartMap.get(itemId) + amount);
                                } else {
                                    order.cartMap.put(itemId, amount);
                                    order.addStock(item);
                                }

                                Integer old = order.getOrderTotal();
                                order.setOrderTotal(old + item.getPrice() * amount);
                                orderRepository.save(order);
                                return true;
                            }).orElseThrow(() -> new ResourceNotFoundException("Item not found with id " + itemId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @PostMapping("/orders/addItem/{order_id}/{item_id}/{amount}")
    public void addItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId, @PathVariable("amount") Integer amount) {
        orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                Integer oldUnits = item.getUnits();
                                if (oldUnits - amount >= 0) {
                                    item.setUnits(oldUnits - amount);
                                    stockRepository.save(item);
                                } else
                                    return false;

                                if (order.cartMap.containsKey(itemId)) {
                                    order.cartMap.put(itemId, order.cartMap.get(itemId) + amount);
                                } else {
                                    order.cartMap.put(itemId, amount);
                                    order.addStock(item);
                                }

                                Integer old = order.getOrderTotal();
                                order.setOrderTotal(old + item.getPrice() * amount);
                                orderRepository.save(order);
                                return true;
                            }).orElseThrow(() -> new ResourceNotFoundException("Item not found with id " + itemId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @DeleteMapping("/orders/removeItem/{order_id}/{item_id}")
    public void removeItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId) {
        int amount = 1;
        orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                if (order.cartMap.containsKey(itemId)) {
                                    Integer newAmount = order.cartMap.get(itemId) + amount;
                                    if (newAmount < 0) return false;
                                    if (newAmount == 0) {
                                        order.cartMap.remove(itemId);
                                    } else {
                                        order.cartMap.put(itemId, newAmount);
                                    }
                                } else {
                                    return false;
                                }

                                Integer oldUnits = item.getUnits();
                                item.setUnits(oldUnits + amount);
                                stockRepository.save(item);

                                Integer old = order.getOrderTotal();
                                order.setOrderTotal(old - item.getPrice() * amount);
                                orderRepository.save(order);
                                return true;
                            }).orElseThrow(() -> new ResourceNotFoundException("Item not found with id " + itemId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @DeleteMapping("/orders/removeItem/{order_id}/{item_id}/{amount}")
    public void removeItem(@PathVariable("order_id") UUID orderId, @PathVariable("item_id") Long itemId, @PathVariable("amount") Integer amount) {
        orderRepository.findById(orderId)
                .map(order -> {
                    return stockRepository.findById(itemId)
                            .map(item -> {
                                if (order.cartMap.containsKey(itemId)) {
                                    Integer newAmount = order.cartMap.get(itemId) + amount;
                                    if (newAmount < 0) return false;
                                    if (newAmount == 0) {
                                        order.cartMap.remove(itemId);
                                    } else {
                                        order.cartMap.put(itemId, newAmount);
                                    }
                                } else {
                                    return false;
                                }

                                Integer oldUnits = item.getUnits();
                                item.setUnits(oldUnits + amount);
                                stockRepository.save(item);

                                Integer old = order.getOrderTotal();
                                order.setOrderTotal(old - item.getPrice() * amount);
                                orderRepository.save(order);
                                return true;
                            }).orElseThrow(() -> new ResourceNotFoundException("Item not found with id " + itemId));
                }).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @GetMapping("/orders/find/{order_id}")
    public Map<String, Object> findOrder(@PathVariable("order_id") UUID orderId) {
        // Boolean paid = orderRepository.findById(orderId)
        //         .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getPaid();
        // Long userId = orderRepository.findById(orderId)
        //         .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getUser().getUserId();
        // Integer totalCost = orderRepository.findById(orderId)
        //         .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getOrderTotal();
        // String cartId = orderRepository.findById(orderId)
        //         .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).cartMap.toString();

        // String stockId = orderRepository.findById(orderId)
        //         .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId)).getStockId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        Map<String, Object> map = new HashMap<>();
        map.put("order_id", orderId);
        map.put("paid", order.getPaid());
        map.put("items", order.getItemIds());
        map.put("user_id", order.getUserId());
        map.put("total_cost", order.getOrderTotal());
        return map;
    }

    @PostMapping("/orders/checkout/{order_id}")
    public void checkout(@PathVariable("order_id") UUID orderId) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        Integer totalCost = order.getOrderTotal();
        Long userId = order.getUser().getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        if (user.getCredit() >= totalCost && !order.getPaid()) {
            user.subtract(order.getOrderTotal());
            userRepository.save(user);
            order.setPaid(true);
            orderRepository.save(order);
        } else {
            throw new Exception("Not enough credit");
        }
    }
}
