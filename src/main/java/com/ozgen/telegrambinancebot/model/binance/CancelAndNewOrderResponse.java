package com.ozgen.telegrambinancebot.model.binance;

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

    public UUID getId() {
        return id;
    }

    public String getCancelResult() {
        return cancelResult;
    }

    public void setCancelResult(String cancelResult) {
        this.cancelResult = cancelResult;
    }

    public String getNewOrderResult() {
        return newOrderResult;
    }

    public void setNewOrderResult(String newOrderResult) {
        this.newOrderResult = newOrderResult;
    }

    public OrderDetails getCancelResponse() {
        return cancelResponse;
    }

    public void setCancelResponse(OrderDetails cancelResponse) {
        this.cancelResponse = cancelResponse;
    }

    public OrderDetails getNewOrderResponse() {
        return newOrderResponse;
    }

    public void setNewOrderResponse(OrderDetails newOrderResponse) {
        this.newOrderResponse = newOrderResponse;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
