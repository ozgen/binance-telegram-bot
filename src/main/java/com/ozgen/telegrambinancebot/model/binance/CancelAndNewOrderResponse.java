package com.ozgen.telegrambinancebot.model.binance;

import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@ToString
public class CancelAndNewOrderResponse {
    @Id
    @GeneratedValue
    private UUID id;

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
