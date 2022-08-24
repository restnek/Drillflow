package com.hashmapinc.tempus.witsml.valve.mock.csv;

import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogData;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.witsml.valve.mock.MockObjLogsGenerator;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvObjLogsGenerator extends MockObjLogsGenerator {

    public CsvObjLogsGenerator(Map<String, String> config) {
        super(config);
    }

    @Override
    protected CsLogData buildLogData(ObjLog log) {
        Instant startDateTime = Instant.parse(log.getStartDateTimeIndex());
        Instant endDateTime = Instant.parse(log.getEndDateTimeIndex());
        int firstSecondOfDay = LocalTime.ofInstant(startDateTime, ZoneOffset.UTC).toSecondOfDay();
        int durationInSeconds = (int) Duration.between(startDateTime, endDateTime).getSeconds();

        CsvConfig csvConfig = new CsvConfig(config);
        CsvDrillState drillState = new CsvDrillState(csvConfig, log);

        List<String> data = new ArrayList<>();
        int firstLine = csvConfig.getCsvFirstLine();
        int lineAmount = csvConfig.getCsvLineAmount();
        int lastLine = firstLine + lineAmount;
        int currentLine = firstLine + firstSecondOfDay % lineAmount;
        while (durationInSeconds > 0) {
            int size = Math.min(lastLine - currentLine, durationInSeconds);

            List<String> values = drillState.read(currentLine, size);
            data.addAll(values);

            durationInSeconds -= values.size();
            currentLine += values.size();
            if (currentLine >= lastLine) {
                currentLine = firstLine;
            }
        }

        CsLogData logData = new CsLogData();
        logData.setData(data);
        return logData;
    }
}
