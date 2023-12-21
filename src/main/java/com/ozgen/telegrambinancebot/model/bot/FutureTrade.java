package com.ozgen.telegrambinancebot.model.bot;

import com.ozgen.telegrambinancebot.model.TradeStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;
import java.util.UUID;

@Entity
public class FutureTrade {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID tradeSignalId;

    private TradeStatus tradeStatus;

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

    public UUID getTradeSignalId() {
        return tradeSignalId;
    }

    public void setTradeSignalId(UUID tradeSignalId) {
        this.tradeSignalId = tradeSignalId;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Override
    public String toString() {
        return "FutureTrade{" +
                "id=" + id +
                ", tradeSignalId=" + tradeSignalId +
                ", tradeStatus=" + tradeStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
