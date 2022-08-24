package com.hashmapinc.tempus.witsml.valve.mock.random;

import java.util.Random;

public class RandomNumericState implements FieldState {
    private static final Random RANDOM = new Random();

    private final int value = RANDOM.nextInt(100);

    @Override
    public FieldState mutate() {
        return new RandomNumericState();
    }

    @Override
    public String toDataString() {
        return Double.toString(value);
    }
}
