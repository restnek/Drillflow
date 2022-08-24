package com.hashmapinc.tempus.witsml.valve.mock.random;

public interface FieldState {
    FieldState mutate();

    String toDataString();
}
