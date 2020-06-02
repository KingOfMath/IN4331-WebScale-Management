package com.yugabyte.springdemo.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long stockId;

    private Integer units;

    //@NotBlank
    private Integer price;


    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Boolean addUnits(Integer units) {
        this.units += units;
        return true;
    }

    public Boolean subtractUnits(Integer units) {
        try{
            if (this.units >= units)
                this.units -= units;
            return true;
        }catch (Exception e){
            System.out.println("Large shabi!");
            e.printStackTrace();
        }

        return false;
    }
}