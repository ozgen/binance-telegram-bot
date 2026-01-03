package com.ozgen.telegrambinancebot.model.binance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "open_order")
@Data
@ToString
public class OpenOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String symbol;
    private String origClientOrderId;
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    private Long transactTime;
    private String price;
    private String origQty;
    private String executedQty;
    private String cummulativeQuoteQty;
    private String status;
    private String timeInForce;
    private String type;
    private String side;
    private String selfTradePreventionMode;
    private String contingencyType;
    private String listStatusType;
    private String listOrderStatus;
    private String listClientOrderId;
    private Long transactionTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "openOrder")
    @JsonProperty("orders")
    private List<OpenOrderDetail> orders;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "openOrder")
    @JsonProperty("orderReports")
    private List<OpenOrderReport> openOrderReports;
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
