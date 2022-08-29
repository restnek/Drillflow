package com.hashmapinc.tempus.witsml.valve.mock.random;

import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.witsml.valve.mock.Field;
import java.time.Instant;
import java.util.Map;
import java.util.function.BiFunction;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FieldStates {

    public static final Map<Field, BiFunction<ObjLog, Map<String, String>, FieldState>> FIELD_STATE_CREATORS =
            Map.ofEntries(
                    Map.entry(Field.TIME, FieldStates::timeStateWithPlusOneSecondMutator),
                    Map.entry(Field.DRILL_PRESSURE, FieldStates::numericState),
                    Map.entry(Field.MUD_WEIGHT_INPUT, FieldStates::numericState),
                    Map.entry(Field.MUD_WEIGHT_OUTPUT, FieldStates::numericState),
                    Map.entry(Field.MUD_USAGE_INPUT, FieldStates::numericState),
                    Map.entry(Field.MUD_USAGE_OUTPUT, FieldStates::numericState),
                    Map.entry(Field.HOOK_WEIGHT, FieldStates::numericState),
                    Map.entry(Field.GAS_OIL_RATIO, FieldStates::numericState),
                    Map.entry(Field.BIT_DEPTH, FieldStates::numericState),
                    Map.entry(Field.DRILL_DEPTH, FieldStates::numericState),
                    Map.entry(Field.ROTATION_TORQUE, FieldStates::numericState),
                    Map.entry(Field.BIT_PRESSURE, FieldStates::numericState),
                    Map.entry(Field.TURNING, FieldStates::numericState),
                    Map.entry(Field.BLOCK_POSITION, FieldStates::numericState),
                    Map.entry(Field.DRILLING_SPEED, FieldStates::numericState),
                    Map.entry(Field.TOTAL_VOLUME, FieldStates::numericState),
                    Map.entry(Field.EQUIVALENT_CIRCULATING_DENSITY, FieldStates::numericState),
                    Map.entry(Field.TORQUE_ON_MACHINE_KEY, FieldStates::numericState),
                    Map.entry(Field.DISPLACEMENT_VOLUME, FieldStates::numericState),
                    Map.entry(Field.TOPPING_UP_VOLUME, FieldStates::numericState),
                    Map.entry(Field.LIFTING_SPEED, FieldStates::numericState),
                    Map.entry(Field.PUMP_STROKE_FREQUENCY_1, FieldStates::numericState),
                    Map.entry(Field.PUMP_STROKE_FREQUENCY_2, FieldStates::numericState),
                    Map.entry(Field.NUMBER_OF_CANDLES, FieldStates::numericState),
                    Map.entry(Field.TEMPERATURE_INPUT, FieldStates::numericState),
                    Map.entry(Field.TEMPERATURE_OUTPUT, FieldStates::numericState),
                    Map.entry(Field.CAPACITY_UNDER_SHALE_SHAKERS, FieldStates::numericState)
            );

    public static final EmptyState EMPTY_FIELD_STATE = new EmptyState();

    public static PlusOneSecondTimeState timeStateWithPlusOneSecondMutator(
            ObjLog log,
            Map<String, String> config
    ) {
        return new PlusOneSecondTimeState(
                Instant.parse(log.getStartDateTimeIndex()));
    }

    public static RandomNumericState numericState(ObjLog log, Map<String, String> config) {
        return new RandomNumericState();
    }
}
