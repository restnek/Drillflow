package com.hashmapinc.tempus.witsml.valve.mock;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ObjectSelectionConstants;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockObjLogsValve implements IValve {
    private final MockObjLogsGenerator mockObjLogsGenerator;

    @Override
    public String getName() {
        return "mock";
    }

    @Override
    public String getDescription() {
        return "Valve for interaction with mock data";
    }

    @Override
    public CompletableFuture<String> getObject(QueryContext qc) throws ValveException {
        try {
            String object = mockObjLogsGenerator.buildObject(qc);
            return CompletableFuture.completedFuture(object);
        } catch (Exception exception) {
            throw new ValveException(exception);
        }
    }

    @Override
    public String getObjectSelectionCapability(String wmlTypeIn) {
        switch (wmlTypeIn) {
            case "well":
                return ObjectSelectionConstants.WELL_OBJ_SELECTION;
            case "wellbore":
                return ObjectSelectionConstants.WELLBORE_OBJ_SELECTION;
            case "trajectory":
                return ObjectSelectionConstants.TRAJECTORY_OBJ_SELECTION;
            default:
                return null;
        }
    }

    @Override
    public CompletableFuture<String> createObject(QueryContext qc) {
        throw new UnsupportedOperationException("Operation WMLS_AddToStore not supported");
    }

    @Override
    public CompletableFuture<Boolean> deleteObject(QueryContext qc) {
        throw new UnsupportedOperationException("Operation WMLS_DeleteFromStore not supported");
    }

    @Override
    public CompletableFuture<Boolean> updateObject(QueryContext qc) {
        throw new UnsupportedOperationException("Operation WMLS_UpdateInStore not supported");
    }

    @Override
    public void authenticate(String userName, String password) {
        // ignore authentication
    }

    @Override
    public Map<String, AbstractWitsmlObject[]> getCap() {
        AbstractWitsmlObject well = new ObjWell();
        AbstractWitsmlObject wellbore = new ObjWellbore();
        AbstractWitsmlObject trajectory = new ObjTrajectory();
        AbstractWitsmlObject log = new ObjLog();
        AbstractWitsmlObject fluidsReport = new ObjFluidsReport();

        return Map.of(
                "WMLS_GetFromStore",
                new AbstractWitsmlObject[] {well, wellbore, trajectory, log, fluidsReport});
    }
}
