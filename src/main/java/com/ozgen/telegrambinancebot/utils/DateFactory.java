package com.ozgen.telegrambinancebot.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateFactory {

    public static Date getDateBeforeInMonths(int monthBefore) {
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(monthBefore);
        return Date.from(oneMonthAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
