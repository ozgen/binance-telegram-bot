package com.ozgen.telegrambinancebot.model.binance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Entity
@Data
@ToString
public class CancelAndNewOrderResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String cancelResult;
    private String newOrderResult;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderDetails cancelResponse;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderDetails newOrderResponse;


    private Date createdAt;
    private Date updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

}
