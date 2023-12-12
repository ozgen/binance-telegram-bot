package com.ozgen.telegrambinancebot.model.telegram;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class TradingSignal {

    @Id
    @GeneratedValue
    private UUID id;
    @NotNull
    private String symbol;
    @NotNull
    private String entryStart;
    @NotNull
    private String entryEnd;
    @ElementCollection
    private List<String> takeProfits;
    @NotNull
    private String stopLoss;

    private Date createdAt;

    private Date updatedAt;

    private int isProcessed;

    public TradingSignal() {
    }

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

    public String getEntryStart() {
        return entryStart;
    }

    public void setEntryStart(String entryStart) {
        this.entryStart = entryStart;
    }

    public String getEntryEnd() {
        return entryEnd;
    }

    public void setEntryEnd(String entryEnd) {
        this.entryEnd = entryEnd;
    }

    public List<String> getTakeProfits() {
        return takeProfits;
    }

    public void setTakeProfits(List<String> takeProfits) {
        this.takeProfits = takeProfits;
    }

    public String getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(String stopLoss) {
        this.stopLoss = stopLoss;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(int isProcessed) {
        this.isProcessed = isProcessed;
    }

    @Override
    public String toString() {
        return "TradingSignal{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", entryStart='" + entryStart + '\'' +
                ", entryEnd='" + entryEnd + '\'' +
                ", takeProfits=" + takeProfits +
                ", stopLoss='" + stopLoss + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
