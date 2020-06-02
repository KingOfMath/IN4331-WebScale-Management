package com.yugabyte.springdemo.model;


import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long paymentId;

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
