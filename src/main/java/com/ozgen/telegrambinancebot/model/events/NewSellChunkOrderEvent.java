package com.ozgen.telegrambinancebot.model.events;

import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class NewSellChunkOrderEvent extends ApplicationEvent {
    private final ChunkOrder chunkOrder;

    public NewSellChunkOrderEvent(Object source,  ChunkOrder chunkOrder) {
        super(source);
        this.chunkOrder = chunkOrder;
    }
}
