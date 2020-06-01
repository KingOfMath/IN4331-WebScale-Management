package com.yugabyte.springdemo.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "orders")
public class Order extends AuditModel {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Transient
	private Long userId;
	
	@NotBlank
	private Integer orderTotal;
	
	@Transient
	@ElementCollection
	private Set<Stock> stocks = new HashSet<>();

	private Boolean paid;

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public UUID getOrderId() {
		return orderId;
	}
	
	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Long getUserId() {
		return this.userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Integer getOrderTotal() {
		return orderTotal;
	}
	
	public void setOrderTotal(Integer orderTotal) {
		this.orderTotal = orderTotal;
	}
	
	public Set<Stock> getStocks() {
		return stocks;
	}
	
	public void setStocks(Set<Stock> stocks) {
		this.stocks = stocks;
	}

	public void addStock(Stock stock){
	    this.stocks.add(stock);
    }

    public void removeStock(Stock stock){
	    this.stocks.remove(stock);
    }

    public String getStockId(){
        StringBuilder s = new StringBuilder(" ");
        for (Stock stock: this.stocks) {
            s.append(stock.getStockId()).append(" ");
        }
        return s.toString();
    }
}
