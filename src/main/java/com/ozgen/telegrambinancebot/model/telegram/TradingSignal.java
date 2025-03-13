package com.ozgen.telegrambinancebot.model.telegram;

import com.ozgen.telegrambinancebot.model.TradingStrategy;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
    private String investAmount;

    private Date createdAt;

    private Date updatedAt;

    private int isProcessed;

    public TradingSignal() {
        this.strategy = TradingStrategy.DEFAULT;
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
