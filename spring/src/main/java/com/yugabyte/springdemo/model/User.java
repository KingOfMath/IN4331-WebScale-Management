package com.yugabyte.springdemo.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long userId;

    private int credit;

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Boolean subtract(int credit) {
        if (this.credit >= credit) {
            this.credit -= credit;
            return true;
        }
        return false;
    }

    public Boolean add(int credit) {
        this.credit += credit;
        return true;
    }

}
