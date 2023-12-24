package com.ozgen.telegrambinancebot.model.binance;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TickerData {

    @Id
    @GeneratedValue
    private UUID id;
    private String symbol;
    private String openPrice;
    private String highPrice;
    private String lowPrice;
    private String lastPrice;
    private String volume;
    private String quoteVolume;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date openTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date closeTime;

    private Integer firstId;
    private Integer lastId;
    private Integer count;

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
