package com.hashmapinc.tempus.witsml.valve.mock.random;

import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.witsml.valve.mock.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomDrillState {
    private final Duration duration;
    private final List<FieldState> fieldStates;

    public RandomDrillState(ObjLog log, Map<String, String> config) {
        Instant currentDateTime = Instant.parse(log.getStartDateTimeIndex());
        Instant endDateTime = Instant.parse(log.getEndDateTimeIndex());
        duration = Duration.between(currentDateTime, endDateTime);
        fieldStates = log.getLogCurveInfo().stream()
                .map(CsLogCurveInfo::getUid)
                .map(Field::byUid)
                .map(o ->
                        o.map(FieldStates.FIELD_STATE_CREATORS::get)
                                .map(f -> f.apply(log, config))
                                .orElse(FieldStates.EMPTY_FIELD_STATE))
                .collect(Collectors.toList());
    }

    public boolean canMutate() {
        return !duration.isZero();
    }

    public RandomDrillState mutate() {
        Duration mutatedDuration = duration.minusSeconds(1);
        List<FieldState> mutatedFieldStates = fieldStates.stream()
                .map(FieldState::mutate)
                .collect(Collectors.toList());
        return new RandomDrillState(mutatedDuration, mutatedFieldStates);
    }

    public String toDataString() {
        return fieldStates.stream()
                .map(FieldState::toDataString)
                .collect(Collectors.joining(","));
    }
}
