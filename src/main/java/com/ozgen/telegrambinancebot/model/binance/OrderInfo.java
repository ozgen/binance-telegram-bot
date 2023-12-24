package com.ozgen.telegrambinancebot.model.binance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class OrderInfo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String symbol;

    private Long orderId;

    private Long orderListId;

    private String clientOrderId;

    private String price;

    private String origQty;

    private String executedQty;

    private String cummulativeQuoteQty;

    private String status;

    private String timeInForce;

    private String type;

    private String side;

    private String stopPrice;

    private String icebergQty;

    @JsonProperty("time")
    private Long orderTime;

    @JsonProperty("updateTime")
    private Long lastUpdateTime;

    @JsonProperty("isWorking")
    private Boolean working;

    private String origQuoteOrderQty;

    private Long workingTime;

    private String selfTradePreventionMode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
