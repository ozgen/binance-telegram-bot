package com.ozgen.telegrambinancebot.model.binance;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KlineData {

    private String symbol;

    private long openTime;                // Kline open time
    private String openPrice;             // Open price
    private String highPrice;             // High price
    private String lowPrice;              // Low price
    private String closePrice;            // Close price
    private String volume;                // Volume
    private long closeTime;               // Kline close time
    private double quoteAssetVolume;      // Quote asset volume
    private int numberOfTrades;           // Number of trades
    private String takerBuyBaseAssetVolume;  // Taker buy base asset volume
    private String takerBuyQuoteAssetVolume; // Taker buy quote asset volume

    public KlineData(long openTime, String openPrice, String highPrice, String lowPrice, String closePrice,
                     String volume, long closeTime, double quoteAssetVolume, int numberOfTrades,
                     String takerBuyBaseAssetVolume, String takerBuyQuoteAssetVolume) {
        this.openTime = openTime;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.closeTime = closeTime;
        this.quoteAssetVolume = quoteAssetVolume;
        this.numberOfTrades = numberOfTrades;
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }

    public KlineData() {
    }
}
