package com.ozgen.telegrambinancebot.model.binance;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class OrderDetails {

    @Id
    @GeneratedValue
    private UUID id;

    private String symbol;
    private String origClientOrderId;
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactTime;
    private String price;
    private String origQty;
    private String executedQty;
    private String cummulativeQuoteQty;
    private String status;
    private String timeInForce;
    private String type;
    private String side;
    private String selfTradePreventionMode;
    @ElementCollection
    private List<String> fills;

    private Long workingTime;

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
