package com.hashmapinc.tempus.witsml.valve.mock.random;

public class EmptyState implements FieldState {

    @Override
    public FieldState mutate() {
        return this;
    }

    @Override
    public String toDataString() {
        return "";
    }
}
