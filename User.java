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

    @NotBlank
    private Integer credit;

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Boolean subtract(Integer credit) {
        if (this.credit > credit) {
            this.credit -= credit;
            return true;
        }
        return false;
    }

    public Boolean add(Integer credit) {
        this.credit += credit;
        return true;
    }

}
