package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.binance.TickerData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JsonParserTest {


    @Test
    void testJsonObjParsing() throws Exception {
        String json = "{ \"symbol\": \"BNBBTC\", \"openPrice\": \"99.00000000\", \"highPrice\": \"100.00000000\", \"lowPrice\": \"0.10000000\", \"lastPrice\": \"4.00000200\", \"volume\": \"8913.30000000\", \"quoteVolume\": \"15.30000000\", \"openTime\": 1499783499040, \"closeTime\": 1499869899040, \"firstId\": 28385, \"lastId\": 28460, \"count\": 76 }";
        TickerData tickerData = JsonParser.parseTickerJson(json);

        assertNotNull(tickerData);
        assertEquals("BNBBTC", tickerData.getSymbol());
        assertEquals("99.00000000", tickerData.getOpenPrice());
        assertEquals(28385, tickerData.getFirstId());
    }
    @Test
    void testJsonArrayParsing() throws Exception {
        String json = "[{ \"symbol\": \"BNBBTC\", \"openPrice\": \"99.00000000\", \"highPrice\": \"100.00000000\", \"lowPrice\": \"0.10000000\", \"lastPrice\": \"4.00000200\", \"volume\": \"8913.30000000\", \"quoteVolume\": \"15.30000000\", \"openTime\": 1499783499040, \"closeTime\": 1499869899040, \"firstId\": 28385, \"lastId\": 28460, \"count\": 76 }]";
        TickerData tickerData = JsonParser.parseTickerJson(json);

        assertNotNull(tickerData);
        assertEquals("BNBBTC", tickerData.getSymbol());
        assertEquals("99.00000000", tickerData.getOpenPrice());
        assertEquals(28385, tickerData.getFirstId());
    }
}
