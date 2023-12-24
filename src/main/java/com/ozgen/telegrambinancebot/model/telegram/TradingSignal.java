package com.ozgen.telegrambinancebot.model.telegram;

import lombok.Data;
import lombok.ToString;

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
@Data
@ToString
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
}
