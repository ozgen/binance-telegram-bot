package com.ozgen.telegrambinancebot.utils;


import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateFactoryTest {

    @Test
    public void testGetDateBeforeInMonths() {
        int monthsBefore = 3;
        Date result = DateFactory.getDateBeforeInMonths(monthsBefore);

        // Calculate the expected date
        LocalDate expectedLocalDate = LocalDate.now().minusMonths(monthsBefore);
        Date expectedDate = Date.from(expectedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Assert that the year, month, and day are as expected
        LocalDate resultLocalDate = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(expectedLocalDate.getYear(), resultLocalDate.getYear());
        assertEquals(expectedLocalDate.getMonth(), resultLocalDate.getMonth());
        assertEquals(expectedLocalDate.getDayOfMonth(), resultLocalDate.getDayOfMonth());
    }
}
