package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceOpenBuyChunkOrderManager {

    private final BinanceApiManager binanceApiManager;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final ScheduleConfiguration scheduleConfiguration;

    public void processOpenChunkOrders() {
        Date searchDate = DateFactory.getDateBeforeInMonths(scheduleConfiguration.getMonthBefore());
        List<ChunkOrder> pendingChunks = botOrderService.getBuyChunksByStatusesAndDate(
                List.of(OrderStatus.BUY_EXECUTED, OrderStatus.BUY_FAILED), searchDate
        );

        for (ChunkOrder chunk : pendingChunks) {
            try {
                this.processOpenChunk(chunk);
                SyncUtil.pauseBetweenOperations();
            } catch (Exception e) {
                log.error("Error processing open chunk {}: {}", chunk.getId(), e.getMessage(), e);
                this.publisher.publishEvent(new ErrorEvent(this, e));
            }
        }
    }

    void processOpenChunk(ChunkOrder chunk) {
        String symbol = chunk.getSymbol();
        try {
            List<OrderInfo> openOrders = binanceApiManager.getOpenOrders(symbol);

            openOrders.stream()
                    .filter(o -> o.getOrigQty().equalsIgnoreCase(String.valueOf(chunk.getBuyCoinAmount())))
                    .findFirst()
                    .ifPresentOrElse(matching -> {
                        try {
                            Optional<Double> origQtyOpt = GenericParser.getDouble(matching.getOrigQty());
                            Optional<Double> execQtyOpt = GenericParser.getDouble(matching.getExecutedQty());

                            if (origQtyOpt.isEmpty() || execQtyOpt.isEmpty()) {
                                String message = String.format(
                                        "Failed to parse order quantities for chunk %s (origQty=%s, execQty=%s)",
                                        chunk.getId(), matching.getOrigQty(), matching.getExecutedQty()
                                );
                                log.warn(message);
                                publisher.publishEvent(new ErrorEvent(this, new IllegalArgumentException(message)));
                                return;
                            }

                            double remaining = origQtyOpt.get() - execQtyOpt.get();

                            binanceApiManager.cancelAndNewOrderWithStopLoss(
                                    symbol, chunk.getBuyPrice(), remaining, matching.getOrderId()
                            );

                            log.info("Recovered chunk {} with new order", chunk.getId());

                            chunk.setStatus(OrderStatus.BUY_EXECUTED);
                            botOrderService.saveChunkOrder(chunk);

                            publisher.publishEvent(new NewSellChunkOrderEvent(this, chunk));

                        } catch (Exception innerEx) {
                            log.error("Error processing matching order for chunk {}: {}", chunk.getId(), innerEx.getMessage(), innerEx);
                            publisher.publishEvent(new ErrorEvent(this, innerEx));
                        }
                    }, () -> {
                        log.info("No matching open order found for chunk {}", chunk.getId());
                    });

        } catch (Exception e) {
            log.error("Error retrieving open orders for chunk {}: {}", chunk.getId(), e.getMessage(), e);
            publisher.publishEvent(new ErrorEvent(this, e));
        }
    }
}
