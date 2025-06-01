package com.ozgen.telegrambinancebot.model.telegram;

import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;


import java.util.Date;
import java.util.List;

@Entity
@Data
@ToString
public class TradingSignal {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String symbol;
    private String entryStart;
    private String entryEnd;
    @ElementCollection
    private List<String> takeProfits;
    private String stopLoss;
    @Enumerated(EnumType.STRING)
    private TradingStrategy strategy;
    @Enumerated(EnumType.STRING)
    private ExecutionStrategy executionStrategy;
    private String investAmount;

    private Date createdAt;

    private Date updatedAt;

    private int isProcessed;

    public TradingSignal() {
        this.strategy = TradingStrategy.DEFAULT;
        this.executionStrategy = ExecutionStrategy.DEFAULT;
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
}
