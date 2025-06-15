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

    private double entryPoint;            // specific entry used in this chunk
    private double leverage;              // calculated leverage per chunk
    private int takeProfitIndex;          // which TP this chunk targets (e.g. 0 = TP1)
    private int takeProfitAllocation;     // percent of coin to sell at this TP
    private int totalChunkCount;   // total chunks expected for this signal

    private int chunkIndex;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "trading_signal_id")
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
