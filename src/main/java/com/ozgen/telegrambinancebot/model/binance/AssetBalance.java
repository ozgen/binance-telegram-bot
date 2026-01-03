package com.ozgen.telegrambinancebot.model.binance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Entity
@Data
@ToString
public class AssetBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String asset;
    private String free;
    private String locked;
    @Column(name = "\"freeze\"")
    private String freeze;
    private String withdrawing;
    private String ipoable;
    private String btcValuation;


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
