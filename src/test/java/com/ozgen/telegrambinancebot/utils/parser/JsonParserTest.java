package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JsonParserTest {

    @Test
    void testJsonObjParsing_withValidJson() throws Exception {
        String json = "{ \"symbol\": \"BNBBTC\", \"openPrice\": \"99.00000000\", \"highPrice\": \"100.00000000\", \"lowPrice\": \"0.10000000\", \"lastPrice\": \"4.00000200\", \"volume\": \"8913.30000000\", \"quoteVolume\": \"15.30000000\", \"openTime\": 1499783499040, \"closeTime\": 1499869899040, \"firstId\": 28385, \"lastId\": 28460, \"count\": 76 }";
        TickerData tickerData = JsonParser.parseTickerJson(json);

        assertNotNull(tickerData);
        assertEquals("BNBBTC", tickerData.getSymbol());
        assertEquals("99.00000000", tickerData.getOpenPrice());
        assertEquals(28385, tickerData.getFirstId());
    }
    @Test
    void testJsonArrayParsing_withValidJson() throws Exception {
        String json = "[{ \"symbol\": \"BNBBTC\", \"openPrice\": \"99.00000000\", \"highPrice\": \"100.00000000\", \"lowPrice\": \"0.10000000\", \"lastPrice\": \"4.00000200\", \"volume\": \"8913.30000000\", \"quoteVolume\": \"15.30000000\", \"openTime\": 1499783499040, \"closeTime\": 1499869899040, \"firstId\": 28385, \"lastId\": 28460, \"count\": 76 }]";
        TickerData tickerData = JsonParser.parseTickerJson(json);

        assertNotNull(tickerData);
        assertEquals("BNBBTC", tickerData.getSymbol());
        assertEquals("99.00000000", tickerData.getOpenPrice());
        assertEquals(28385, tickerData.getFirstId());
    }

    @Test
    public void testParseSnapshotJson_withValidJson() throws Exception {
        String jsonString = "{\"code\":200,\"msg\":\"\",\"snapshotVos\":[{\"type\":\"spot\",\"updateTime\":1700351999000,\"data\":{\"totalAssetOfBtc\":\"0.00270573\",\"balances\":[{\"asset\":\"ADX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"ATM\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"AUDIO\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BCHSV\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BNB\",\"free\":\"0.25760623\",\"locked\":\"0\"},{\"asset\":\"BTC\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BTS\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BUSD\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"CHESS\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"CTSI\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DATA\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DGB\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DOCK\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DUSK\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FOR\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FTM\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FUN\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"GALA\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"IDEX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"IOTX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"KEY\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"KMD\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"MBOX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"PEOPLE\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"REEF\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"SALT\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"SC\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"TRIBE\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"USDT\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"WAVES\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"WAXP\",\"free\":\"516.483\",\"locked\":\"0\"},{\"asset\":\"XRP\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"XVG\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"ZRX\",\"free\":\"0\",\"locked\":\"0\"}]}}]}";

        SnapshotData result = JsonParser.parseSnapshotJson(jsonString);

        assertNotNull(result);
        assertNotNull(result.getSnapshotVos());
        assertFalse(result.getSnapshotVos().isEmpty());

        SnapshotData.SnapshotVo firstSnapshot = result.getSnapshotVos().get(0);
        assertEquals("spot", firstSnapshot.getType());
        assertNotNull( firstSnapshot.getData());
        assertEquals("0.00270573", firstSnapshot.getData().getTotalAssetOfBtc());

        List<SnapshotData.Balance> balances = firstSnapshot.getData().getBalances();
        assertNotNull( balances);
        assertFalse("Balances should not be empty", balances.isEmpty());

        SnapshotData.Balance firstBalance = balances.get(0);
        assertEquals( "ADX", firstBalance.getAsset());
        assertEquals("0", firstBalance.getFree());
        assertEquals("0", firstBalance.getLocked());

    }
}
