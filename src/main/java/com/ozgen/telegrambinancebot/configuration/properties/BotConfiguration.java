package com.ozgen.telegrambinancebot.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.bot.investment")
public class BotConfiguration {

    private double amount;
    private double perAmount;
    private double percentageInc;
    private double profitPercentage;
    private String currency;
    private String currencyRate;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(String currencyRate) {
        this.currencyRate = currencyRate;
    }

    public double getPerAmount() {
        return perAmount;
    }

    public void setPerAmount(double perAmount) {
        this.perAmount = perAmount;
    }

    public double getPercentageInc() {
        return percentageInc;
    }

    public void setPercentageInc(double percentageInc) {
        this.percentageInc = percentageInc;
    }

    public double getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(double profitPercentage) {
        this.profitPercentage = profitPercentage;
    }
}
