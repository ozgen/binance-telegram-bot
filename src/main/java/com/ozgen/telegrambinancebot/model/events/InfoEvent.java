package com.ozgen.telegrambinancebot.model.events;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class InfoEvent extends ApplicationEvent {
    private final String message;

    public InfoEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

}
