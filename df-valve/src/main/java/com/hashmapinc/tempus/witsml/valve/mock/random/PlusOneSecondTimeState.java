package com.hashmapinc.tempus.witsml.valve.mock.random;

import com.hashmapinc.tempus.witsml.valve.mock.TimeUtil;
import java.time.Instant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlusOneSecondTimeState implements FieldState {

    private final Instant dateTime;

    @Override
    public FieldState mutate() {
        return new PlusOneSecondTimeState(dateTime.plusSeconds(1));
    }

    @Override
    public String toDataString() {
        return TimeUtil.format(dateTime);
    }
}
