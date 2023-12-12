package com.ozgen.telegrambinancebot.model.bot;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;

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
public class BuyOrder {

    @Id
    @GeneratedValue
    private UUID id;

    private String symbol;

    private double coinAmount;
    private double stopLossLimit;
    private double stopLoss;
    private double buyPrice;
    private Integer times;
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


    public UUID getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(double coinAmount) {
        this.coinAmount = coinAmount;
    }

    public double getStopLossLimit() {
        return stopLossLimit;
    }

    public void setStopLossLimit(double stopLossLimit) {
        this.stopLossLimit = stopLossLimit;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public TradingSignal getTradingSignal() {
        return tradingSignal;
    }

    public void setTradingSignal(TradingSignal tradingSignal) {
        this.tradingSignal = tradingSignal;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "BuyOrder{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", coinAmount=" + coinAmount +
                ", stopLossLimit=" + stopLossLimit +
                ", stopLoss=" + stopLoss +
                ", buyPrice=" + buyPrice +
                ", times=" + times +
                ", tradingSignal=" + tradingSignal +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
