package com.ozgen.telegrambinancebot.model.events;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class ErrorEvent extends ApplicationEvent {
    private final Exception exception;

    public ErrorEvent(Object source, Exception exception) {
        super(source);
        this.exception = exception;
    }

}
