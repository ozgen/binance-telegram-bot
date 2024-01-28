package com.ozgen.telegrambinancebot.model.events;

import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
public class InfoEvent extends ApplicationEvent {
    private final String message;

    public InfoEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
