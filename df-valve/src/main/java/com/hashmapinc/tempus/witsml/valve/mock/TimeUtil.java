package com.hashmapinc.tempus.witsml.valve.mock;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtil {
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendInstant(0)
            .toFormatter();

    public String format(Instant instant) {
        return FORMATTER.format(instant);
    }
}
