package com.ozgen.telegrambinancebot.model.bot;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Data
@ToString
public class ChunkOrder {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String symbol;
    private String sellSymbol;

    private double buyCoinAmount;
    private double sellCoinAmount;
    private double stopLoss;
    private double buyPrice;
    private double sellPrice;

    private int chunkIndex;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
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
