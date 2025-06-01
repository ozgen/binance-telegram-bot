package com.ozgen.telegrambinancebot.manager.binance;


import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BinanceHelperTest {

    private static final String SYMBOL = "BNBBTC";
    private static final String LAST_PRICE = "0.01";
    private static final String LAST_PRICE_OUT_RANGE = "0.02";
    private static final String START_ENTRY = "0.009";
    private static final String END_ENTRY = "0.011";
    private static final String CURRENCY_RATE = "BTCUSD";
    private static final Double AMOUNT = 100.0;
    private static final Double PERCENTAGE_INC = 0.5;
    private static final Double PROFIT_PERCENTAGE = 6.0;
    private static final float BINANCE_FEE_PERCENTAGE = 0.00201f;
    private static final String CURRENCY = "BTC";
    private static final Double BTC_AMOUNT = 0.1;
    private static final String BTCUSD_PRICE = "42000";
    private static final String STOPLOSS = "0.008";


    @Mock
    private BotConfiguration botConfiguration;
    @Mock
    private BinanceApiManager binanceApiManager;
    @Mock
    private ScheduleConfiguration scheduleConfiguration;

    @InjectMocks
    private BinanceHelper binanceHelper;

    @Mock
    private TradingSignal tradingSignal;
    @Mock
    private TickerData tickerDataBTCUSD;
    @Mock
    private AssetBalance assetBalance;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(this.tradingSignal.getSymbol())
                .thenReturn(SYMBOL);
        when(this.tradingSignal.getEntryStart())
                .thenReturn(START_ENTRY);
        when(this.tradingSignal.getEntryEnd())
                .thenReturn(END_ENTRY);
        when(this.tradingSignal.getStopLoss())
                .thenReturn(STOPLOSS);
        when(this.tradingSignal.getInvestAmount())
                .thenReturn(null);

        when(this.botConfiguration.getCurrency())
                .thenReturn(CURRENCY);
        when(this.botConfiguration.getAmount())
                .thenReturn(AMOUNT);
        when(this.botConfiguration.getCurrencyRate())
                .thenReturn(CURRENCY_RATE);
        when(this.botConfiguration.getPercentageInc())
                .thenReturn(PERCENTAGE_INC);
        when(this.botConfiguration.getProfitPercentage())
                .thenReturn(PROFIT_PERCENTAGE);
        when(this.botConfiguration.getBinanceFeePercentage())
                .thenReturn(BINANCE_FEE_PERCENTAGE);

        when(this.binanceApiManager.getTickerPrice24(CURRENCY_RATE))
                .thenReturn(this.tickerDataBTCUSD);
        when(this.tickerDataBTCUSD.getLastPrice())
                .thenReturn(BTCUSD_PRICE);
    }

    @Test
    public void test_hasAccountEnoughAsset_withNullInvest_returnTrue() throws Exception {
        when(this.assetBalance.getAsset())
                .thenReturn(CURRENCY);
        when(this.assetBalance.getFree())
                .thenReturn(String.valueOf(BTC_AMOUNT));

        boolean result = this.binanceHelper.hasAccountEnoughAsset(List.of(this.assetBalance), this.tradingSignal);

        assertTrue(result);
    }

    @Test
    public void test_hasAccountEnoughAsset_withNullInvest_returnFalse() throws Exception {
        when(this.assetBalance.getAsset())
                .thenReturn(CURRENCY);
        when(this.assetBalance.getFree())
                .thenReturn(String.valueOf(0.00001));

        boolean result = this.binanceHelper.hasAccountEnoughAsset(List.of(this.assetBalance), this.tradingSignal);

        assertFalse(result);
    }

    @Test
    public void test_hasAccountEnoughAsset_withInvest_returnTrue() throws Exception {
        when(this.tradingSignal.getInvestAmount())
                .thenReturn(String.valueOf(BTC_AMOUNT));
        when(this.assetBalance.getAsset())
                .thenReturn(CURRENCY);
        when(this.assetBalance.getFree())
                .thenReturn(String.valueOf(BTC_AMOUNT));

        boolean result = this.binanceHelper.hasAccountEnoughAsset(List.of(this.assetBalance), this.tradingSignal);

        assertTrue(result);
    }

    @Test
    public void test_hasAccountEnoughAsset_withInvest_returnFalse() throws Exception {
        when(this.tradingSignal.getInvestAmount())
                .thenReturn(String.valueOf(BTC_AMOUNT));
        when(this.assetBalance.getAsset())
                .thenReturn(CURRENCY);
        when(this.assetBalance.getFree())
                .thenReturn(String.valueOf(0.00001));

        boolean result = this.binanceHelper.hasAccountEnoughAsset(List.of(this.assetBalance), this.tradingSignal);

        assertFalse(result);
    }

    @Test
    public void test_calculateCoinAmount_withNullInvest_ReturnExpectedAmount() throws Exception {
        double expectedBuyPrice = (Double.parseDouble(LAST_PRICE) * (PERCENTAGE_INC / 100) + Double.parseDouble(LAST_PRICE));
        double binanceFee = AMOUNT * BINANCE_FEE_PERCENTAGE;
        double expectedCoinAmount = ((AMOUNT - binanceFee) / Double.parseDouble(BTCUSD_PRICE)) / expectedBuyPrice;

        double result = this.binanceHelper.calculateCoinAmount(expectedBuyPrice, this.tradingSignal);

        assertEquals(expectedCoinAmount, result);
    }

    @Test
    public void test_calculateCoinAmount_withNullInvest_thrownApiException() throws Exception {
        double expectedBuyPrice = (Double.parseDouble(LAST_PRICE) * (PERCENTAGE_INC / 100) + Double.parseDouble(LAST_PRICE));
        when(this.binanceApiManager.getTickerPrice24(CURRENCY_RATE))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class,
                () -> this.binanceHelper.calculateCoinAmount(expectedBuyPrice, this.tradingSignal));
    }

    @Test
    public void test_calculateCoinAmount_withInvest_ReturnExpectedAmount() throws Exception {
        double expectedBuyPrice = (Double.parseDouble(LAST_PRICE) * (PERCENTAGE_INC / 100) + Double.parseDouble(LAST_PRICE));
        double binanceFee = BTC_AMOUNT * BINANCE_FEE_PERCENTAGE;
        double expectedCoinAmount = (BTC_AMOUNT - binanceFee) / expectedBuyPrice;
        when(this.tradingSignal.getInvestAmount())
                .thenReturn(String.valueOf(BTC_AMOUNT));

        double result = this.binanceHelper.calculateCoinAmount(expectedBuyPrice, this.tradingSignal);

        assertEquals(expectedCoinAmount, result);
    }

    @Test
    public void test_getUserAssets_success() throws Exception {
        // Arrange
        List<AssetBalance> mockAssets = List.of(new AssetBalance());
        when(binanceApiManager.getUserAsset()).thenReturn(mockAssets);

        // Act
        List<AssetBalance> result = binanceHelper.getUserAssets();

        // Assert
        assertEquals(mockAssets, result);
    }

    @Test
    public void test_getUserAssets_exceptionReturnsEmptyList() throws Exception {
        // Arrange
        when(binanceApiManager.getUserAsset()).thenThrow(new RuntimeException("API failure"));

        // Act
        List<AssetBalance> result = binanceHelper.getUserAssets();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void test_getSearchDate_returnsExpectedDate() {
        // Arrange
        int monthsBefore = 2;
        when(scheduleConfiguration.getMonthBefore()).thenReturn(monthsBefore);
        Date expectedDate = new Date(0); // example fixed point

        try (MockedStatic<DateFactory> mockedStatic = org.mockito.Mockito.mockStatic(DateFactory.class)) {
            mockedStatic.when(() -> DateFactory.getDateBeforeInMonths(monthsBefore)).thenReturn(expectedDate);

            // Act
            Date result = binanceHelper.getSearchDate();

            // Assert
            assertEquals(expectedDate, result);
        }
    }
}
