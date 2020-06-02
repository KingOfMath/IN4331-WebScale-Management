package com.yugabyte.springdemo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yugabyte.springdemo.exception.ResourceNotFoundException;
import com.yugabyte.springdemo.model.User;
import com.yugabyte.springdemo.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @PostMapping("/users/create")
    public Long createUser(@Valid @RequestBody User user) {
        userRepository.save(user);
        return user.getUserId();
    }

    @GetMapping("/users/find/{user_id}")
    public Map<Long,Integer> getUser(@PathVariable("user_id") Long userId) {
        Map<Long, Integer> map = new HashMap<>();
        Integer credit = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId)).getCredit();
        map.put(userId,credit);
        return map;
    }

    @DeleteMapping("/users/remove/{user_id}")
    public Boolean deleteUser(@PathVariable("user_id") Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    @PostMapping("/users/credit/subtract/{user_id}/{amount}")
    public Boolean subtractCredit(@PathVariable("user_id") Long userId, @PathVariable("amount") Integer credit){
        return userRepository.findById(userId)
                .map(user -> {
                    if(user.subtract(credit)) {
                        userRepository.save(user);
                        return true;
                    } else {
                        try {
                            throw new Exception("error!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    @PostMapping("/users/credit/add/{user_id}/{amount}")
    public Boolean addCredit(@PathVariable("user_id") Long userId, @PathVariable("amount") Integer credit){
        return userRepository.findById(userId)
                .map(user -> {
                    user.add(credit);
                    userRepository.save(user);
                    return true;
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }
}
