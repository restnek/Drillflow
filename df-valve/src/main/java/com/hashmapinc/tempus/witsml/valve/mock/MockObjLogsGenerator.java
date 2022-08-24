package com.hashmapinc.tempus.witsml.valve.mock;

import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogData;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs;
import com.hashmapinc.tempus.witsml.QueryContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.bind.JAXBException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MockObjLogsGenerator {
    protected final Map<String, String> config;

    public String buildObject(QueryContext queryContext) throws JAXBException {
        ObjLogs logs = buildLogs(queryContext);
        return WitsmlMarshal.serialize(logs);
    }

    public ObjLogs buildLogs(QueryContext queryContext) {
        ObjLogs logs = new ObjLogs();

        logs.setVersion(queryContext.CLIENT_VERSION);

        queryContext.WITSML_OBJECTS.stream()
                .filter(ObjLog.class::isInstance)
                .map(ObjLog.class::cast)
                .map(this::buildLog)
                .forEach(logs::addLog);

        return logs;
    }

    private ObjLog buildLog(ObjLog request) {
        ObjLog response = new ObjLog();

        response.setUid(request.getUid());
        response.setUidWell(request.getUidWell());
        response.setUidWellbore(request.getUidWellbore());
        response.setStartDateTimeIndex(request.getStartDateTimeIndex());
        response.setEndDateTimeIndex(request.getEndDateTimeIndex());

        List<CsLogCurveInfo> logCurveInfo = IntStream.range(0, request.getLogCurveInfo().size())
                .mapToObj(i -> buildLogCurveInfo(request.getLogCurveInfo().get(i), i))
                .collect(Collectors.toList());
        response.setLogCurveInfo(logCurveInfo);
        response.setLogData(buildLogData(request));

        return response;
    }

    private CsLogCurveInfo buildLogCurveInfo(CsLogCurveInfo request, int index) {
        CsLogCurveInfo response = new CsLogCurveInfo();

        response.setUid(request.getUid());
        response.setColumnIndex(Integer.toString(index + 1));

        Field.byUid(request.getUid())
                .ifPresent(f -> {
                    response.setCurveDescription(f.getDescription());
                    // response.setMnemonic(f.getMnemonic());
                    // response.setUnit(f.getUnit());
                    // responseLogCurveInfo.setTypeLogData(f.getTypeLogData());
                });

        return response;
    }

    protected abstract CsLogData buildLogData(ObjLog log);
}
