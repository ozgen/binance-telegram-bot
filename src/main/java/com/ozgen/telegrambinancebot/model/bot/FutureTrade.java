package com.ozgen.telegrambinancebot.model.bot;

import com.ozgen.telegrambinancebot.model.TradeStatus;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@ToString
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
}
