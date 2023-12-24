package com.ozgen.telegrambinancebot.model.binance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "open_order")
@Data
@ToString
public class OpenOrder {

    @Id
    @GeneratedValue
    private UUID id;
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
