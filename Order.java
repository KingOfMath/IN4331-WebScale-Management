package com.yugabyte.springdemo.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "orders")
public class Order extends AuditModel {

	public static class Cart {
		private Long cartId;
		private Integer units;
		private Integer price;

		public Long getCartId() {
			return this.cartId;
		}

		public Integer getUnits() {
			return this.units;
		}

		public Integer getPrice(){
			return this.price;
		}

		public void setCartId(Long cartId) {
			this.cartId = cartId;
		}

		public void setUnits(Integer units) {
			this.units = units;
		}

		public void setPrice(Integer price) {
			this.price = price;
		}

		public Cart(Long cartId, Integer units, Integer price){
			this.cartId = cartId;
			this.units = units;
			this.price = price;
		}
	}

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

	private Set<Order.Cart> carts = new HashSet<>();

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

    public String getStockId(){
        StringBuilder s = new StringBuilder(" ");
        for (Stock stock: this.stocks) {
            s.append(stock.getStockId()).append(" ");
        }
        return s.toString();
    }

	public Set<Cart> getCarts() {
		return this.carts;
	}

	public void setCarts(Set<Cart> carts) {
		this.carts = carts;
	}

	public void addCart(Cart cart){
    	this.carts.add(cart);
	}

	public void removeCart(Cart cart){
    	this.carts.remove(cart);
	}

	public String getCartId(){
		StringBuilder s = new StringBuilder(" ");
		for (Cart cart: this.carts) {
			s.append(cart.getCartId()).append(" ");
		}
		return s.toString();
	}
}
