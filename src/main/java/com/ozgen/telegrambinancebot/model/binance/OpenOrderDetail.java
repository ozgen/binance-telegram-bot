package com.ozgen.telegrambinancebot.model.binance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(name = "open_order_detail")
@Data
@ToString
public class OpenOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String symbol;
    private Long orderId;
    private String clientOrderId;
    @ManyToOne
    @JoinColumn(name = "open_order_id")
    private OpenOrder openOrder;

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
