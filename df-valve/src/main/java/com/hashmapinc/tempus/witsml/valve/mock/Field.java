package com.hashmapinc.tempus.witsml.valve.mock;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Field {

    TIME("-1", "Data collection time"),
    DRILL_PRESSURE("1", "Input pressure"),
    MUD_WEIGHT_INPUT("2", "Input drilling mud density"),
    MUD_WEIGHT_OUTPUT("3", "Output drilling mud density"),
    MUD_USAGE_INPUT("4", "Input drilling mud usage"),
    MUD_USAGE_OUTPUT("5", "Output drilling mud usage"),
    HOOK_WEIGHT("6", "Hook weight"),
    GAS_OIL_RATIO("7", "Gas oil ration"),
    BIT_DEPTH("8", "Bit depth"),
    DRILL_DEPTH("9", "Drill depth"),
    ROTATION_TORQUE("10", "Крутящий момент на роторе"),
    BIT_PRESSURE("11", "Big pressure"),
    TURNING("12", "Number of revolutions of the rotor"),
    BLOCK_POSITION("13", "Block position"),
    DRILLING_SPEED("14", "Drilling speed"),
    TOTAL_VOLUME("15", "Total volume"),
    EQUIVALENT_CIRCULATING_DENSITY("16", "Equivalent circulating density"),
    TORQUE_ON_MACHINE_KEY("17", "Torque on the machine key"),
    DISPLACEMENT_VOLUME("18", "Mud displacement volume"),
    TOPPING_UP_VOLUME("19", "Topping up volume"),
    LIFTING_SPEED("20", "Lifting speed"),
    PUMP_STROKE_FREQUENCY_1("21", "Pump 1 - stroke frequency"),
    PUMP_STROKE_FREQUENCY_2("22", "Pump 2 - stroke frequency"),
    NUMBER_OF_CANDLES("23", "Number of candles"),
    TEMPERATURE_INPUT("24", "Input temperature"),
    TEMPERATURE_OUTPUT("25", "Output temperature"),
    CAPACITY_UNDER_SHALE_SHAKERS("26", "Capacity under shale shakers"),
    OPERATION("27", "Operation"),
    PHASE("28", "Phase");

    private static final Map<String, Field> UID_TO_FIELD = Stream.of(Field.values())
            .collect(Collectors.toMap(Field::getUid, Function.identity()));

    private final String uid;
    private final String description;

    public static Optional<Field> byUid(String uid) {
        return Optional.ofNullable(UID_TO_FIELD.get(uid));
    }
}
