package com.ozgen.telegrambinancebot.model.bot;

/**
 * Represents the status of a buy or sell order in the bot lifecycle.
 */
public enum OrderStatus {

    /** The buy order was successfully sent to the Binance API. */
    BUY_EXECUTED,

    /** The buy order failed to send to the Binance API. */
    BUY_FAILED,

    /** The sell order was not sent because the market trend was not favorable. */
    SELL_PENDING_RETRY,

    /** The sell order was successfully sent to the Binance API. */
    SELL_EXECUTED,

    /** The sell order failed to send to the Binance API. */
    SELL_FAILED
}
