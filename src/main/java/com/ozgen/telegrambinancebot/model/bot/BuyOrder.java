package com.ozgen.telegrambinancebot.model.bot;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
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
public class BuyOrder {

    @Id
    @GeneratedValue
    private UUID id;

    private String symbol;

    private double coinAmount;
    private double stopLossLimit;
    private double stopLoss;
    private double buyPrice;
    private int times;
    @OneToOne(cascade = CascadeType.ALL)
    private TradingSignal tradingSignal;

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
