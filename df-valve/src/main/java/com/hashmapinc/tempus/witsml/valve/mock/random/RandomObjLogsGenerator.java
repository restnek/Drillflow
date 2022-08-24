package com.hashmapinc.tempus.witsml.valve.mock.random;

import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogData;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.witsml.valve.mock.MockObjLogsGenerator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomObjLogsGenerator extends MockObjLogsGenerator {
    public RandomObjLogsGenerator(Map<String, String> config) {
        super(config);
    }

    @Override
    protected CsLogData buildLogData(ObjLog log) {
        List<String> values =
                Stream.iterate(new RandomDrillState(log, config), RandomDrillState::canMutate, RandomDrillState::mutate)
                        .map(RandomDrillState::toDataString)
                        .collect(Collectors.toList());
        CsLogData logData = new CsLogData();
        logData.setData(values);
        return logData;
    }
}