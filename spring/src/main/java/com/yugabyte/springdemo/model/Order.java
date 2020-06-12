package com.yugabyte.springdemo.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Map;

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

	private Integer orderTotal;
	
	//@Transient
	@ElementCollection
	private Set<Stock> stocks = new HashSet<>();

	@ElementCollection
	public Map<Long,Integer> cartMap = new HashMap<>();

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
		return this.stocks;
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

    public Set<Long> getItemIds(){
        Set<Long> set = new HashSet<>();
        for (Stock stock : this.stocks) {
            set.add(stock.getStockId());
        }
        return set;
    }

//	public ArrayList<Cart> getCarts() {
//		return this.carts;
//	}
//
//	public void setCarts(ArrayList<Cart> carts) {
//		this.carts = carts;
//	}
//
//	public void addCart(Cart cart){
//    	this.carts.add(cart);
//	}
//
//	public void removeCart(Cart cart){
//    	this.carts.remove(cart);
//	}
//
//	public String getCartId(){
//		StringBuilder s = new StringBuilder(" ");
//		for (Cart cart: this.carts) {
//			s.append(cart.getCartId()).append(" ");
//		}
//		return s.toString();
//	}
}
