package com.yugabyte.springdemo.model;


import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Transient
    private Long userId;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @Transient
    private UUID orderId;

}
