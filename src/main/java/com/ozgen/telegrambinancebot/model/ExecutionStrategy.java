package com.ozgen.telegrambinancebot.model;

public enum ExecutionStrategy {
    DEFAULT,     // Buy/Sell as a single order
    CHUNKED,      // Execute in multiple chunks
    CHUNKED_PROGRESSIVE // todo add explanation
}
