package com.ozgen.telegrambinancebot.model.events;

import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class NewSellOrderEvent extends ApplicationEvent {
    private final BuyOrder buyOrder;

    public NewSellOrderEvent(Object source, BuyOrder buyOrder) {
        super(source);
        this.buyOrder = buyOrder;
    }

}
