package com.ozgen.telegrambinancebot.model;

public enum ExecutionStrategy {
    DEFAULT,     // Buy/Sell as a single order
    CHUNKED,      // Execute in multiple chunks
    CHUNKED_PROGRESSIVE // Executes buy/sell orders in dynamic chunks based on market liquidity (e.g., quote volume).
                        // Suitable for volatile or low-liquidity assets, allowing gradual position building.
                        // The number of chunks is calculated dynamically, and each chunk is placed only if market
                        // conditions (like short-term bullish trend and entry price range) are favorable.
}
