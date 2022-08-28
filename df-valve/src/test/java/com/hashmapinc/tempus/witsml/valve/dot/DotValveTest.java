/*
 * Copyright © 2018-2019 Hashmap, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hashmapinc.tempus.witsml.valve.dot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DotValveTest {
  private DotClient mockClient;
  private DotDelegator mockDelegator;
  private DotValve valve;

  @BeforeEach
  public void doSetup() {
    this.mockClient = mock(DotClient.class);
    this.mockDelegator = mock(DotDelegator.class);
    this.valve = new DotValve(this.mockClient, this.mockDelegator); // inject mocks into valve
  }

  @Test
  void shouldGetName() {
    assertEquals("DoT", this.valve.getName());
  }

  @Test
  void shouldGetDescription() {
    assertEquals("Valve for interaction with Drillops Town",
        this.valve.getDescription());
  }

  @Test
  void shouldGetSingleObject() throws Exception {
    // build well list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();
    ObjWell well = new ObjWell();
    well.setName("well-1");
    well.setUid("well-1");
    witsmlObjects.add(well);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "well",
            new HashMap<>(),
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldGetSingleObject" // exchange ID
            );

    // mock delegator behavior
    when(this.mockDelegator.getObject(
            well, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient, qc.getOptionsIn()))
        .thenReturn(well);

    // test
    String expected = well.getXMLString("1.3.1.1");
    String actual = this.valve.getObject(qc).get();
    assertEquals(expected, actual);
  }

  // code to test getFromStore for Log
  @Test
  void shouldGetLogSingleObject() throws Exception {
    // build well list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();
    ObjLog log = new ObjLog();
    log.setUid("HM_800007");
    log.setUidWell("U2");
    log.setUidWellbore("WBDD600");
    witsmlObjects.add(log);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.4.1.1",
            "log",
            new HashMap<>(),
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldGetSingleObject" // exchange ID
            );

    // mock delegator behavior
    when(this.mockDelegator.getObject(
            log, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient, qc.getOptionsIn()))
        .thenReturn(log);

    // test
    String expected = log.getXMLString("1.4.1.1");
    String actual = this.valve.getObject(qc).get();
    assertEquals(expected, actual);
  }

  @Test
  void shouldGetEmptyObject() throws Exception {
    // build well list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();
    ObjWell well = new ObjWell();
    well.setName("well-1");
    well.setUid("well-1");
    witsmlObjects.add(well);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "well",
            null,
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldGetEmptyObject" // exchange ID
            );

    // mock delegator behavior
    when(this.mockDelegator.getObject(
            well, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient, qc.getOptionsIn()))
        .thenReturn(null);

    // test
    String expected = WitsmlMarshal.serialize(new ObjWells());
    String actual = this.valve.getObject(qc).get();
    assertEquals(expected, actual);
  }

  @Test
  void shouldGetPluralObject() throws Exception {
    // build witsmlObjects list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();

    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore wellboreA =
        new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore();
    wellboreA.setName("wellbore-A");
    wellboreA.setUid("wellbore-A");
    witsmlObjects.add(wellboreA);

    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore wellboreB =
        new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore();
    wellboreB.setName("wellbore-B");
    wellboreB.setUid("wellbore-B");
    witsmlObjects.add(wellboreB);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "wellbore",
            new HashMap<>(),
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldGetPluralObject" // exchange ID
            );

    // mock delegator behavior
    doReturn(wellboreA)
        .when(this.mockDelegator)
        .getObject(
            wellboreA, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient, new HashMap<>());
    doReturn(wellboreB)
        .when(this.mockDelegator)
        .getObject(
            wellboreB, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient, new HashMap<>());

    // test
    String expected = // expected = merge wellboreA and wellbore B
        wellboreA.getXMLString("1.3.1.1").replace("</wellbores>", "")
            + wellboreB
                .getXMLString("1.3.1.1")
                .replace(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                        + "<wellbores version=\"1.3.1.1\" xmlns=\"http://www.witsml.org/schemas/131\">",
                    "");
    String actual = this.valve.getObject(qc).get();
    assertEquals(expected, actual);
  }

  @Test
  void shouldSearchWellsWithEmptyUID() throws Exception {
    String query = TestUtilities.getResourceAsString("well1311FullEmptyQueryNoUid.xml");
    String response = TestUtilities.getResourceAsString("well1311FullEmptyQueryNoUidResponse.xml");
    String completeResponse =
        TestUtilities.getResourceAsString("well1311NoUidSearchFullValveResponse.xml");
    ObjWells wellsResponse = WitsmlMarshal.deserialize(response, ObjWells.class);
    ObjWells wellsQuery = WitsmlMarshal.deserialize(query, ObjWells.class);
    ObjWell singularWellQuery = wellsQuery.getWell().get(0);
    ObjWell singularWellResponse = wellsResponse.getWell().get(0);
    List<AbstractWitsmlObject> wmlObjectsQuery = new ArrayList<>();
    wmlObjectsQuery.add(singularWellQuery);
    List<AbstractWitsmlObject> wmlObjectsResp = new ArrayList<>();
    wmlObjectsResp.add(singularWellResponse);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "well",
            new HashMap<>(),
            query,
            wmlObjectsQuery,
            "goodUsername",
            "goodPassword",
            "shouldGetPluralObject");

    // mock delegator behavior
    doReturn(wmlObjectsResp)
        .when(this.mockDelegator)
        .search(
            singularWellQuery,
            qc.getUsername(),
            qc.getPassword(),
            qc.getExchangeId(),
            this.mockClient,
            new HashMap<>());

    String actual = this.valve.getObject(qc).get();
    assertEquals(completeResponse, actual);
  }

  @Test
  void shouldCreateSingleObject() throws Exception {
    // build witsmlObjects list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();

    ObjWellbore wellboreA = new ObjWellbore();
    wellboreA.setName("wellbore-A");
    wellboreA.setUid("wellbore-A");
    witsmlObjects.add(wellboreA);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "wellbore",
            null,
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldCreateSingleObject" // exchange ID
            );

    // mock delegator behavior
    when(this.mockDelegator.createObject(
            wellboreA, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient))
        .thenReturn(wellboreA.getUid());

    // test
    String expected = wellboreA.getUid();
    String actual = this.valve.createObject(qc).get();
    assertEquals(expected, actual);
  }

  @Test
  void shouldCreateTrajectory() throws Exception {
    // build witsmlObjects list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();

    ObjTrajectory traj = new ObjTrajectory();
    traj.setUid("traj-A");
    traj.setName("traj-A");

    witsmlObjects.add(traj);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "trajectory",
            null,
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldCreateTrajectory" // exchange ID
            );

    // mock delegator behavior
    when(this.mockDelegator.createObject(
            traj, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient))
        .thenReturn(traj.getUid());

    // test
    String expected = traj.getUid();
    String actual = this.valve.createObject(qc).get();
    assertEquals(expected, actual);
  }

  @Test
  void shouldCreatePluralObject() throws Exception {
    // build witsmlObjects list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();

    ObjWellbore wellboreA = new ObjWellbore();
    wellboreA.setName("wellbore-A");
    wellboreA.setUid("wellbore-A");
    witsmlObjects.add(wellboreA);

    ObjWellbore wellboreB = new ObjWellbore();
    wellboreB.setName("wellbore-B");
    wellboreB.setUid("wellbore-B");
    witsmlObjects.add(wellboreB);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "wellbore",
            null,
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldCreatePluralObject" // exchange ID
            );

    // mock delegator behavior
    when(this.mockDelegator.createObject(
            wellboreA, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient))
        .thenReturn(wellboreA.getUid());
    when(this.mockDelegator.createObject(
            wellboreB, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient))
        .thenReturn(wellboreB.getUid());

    // test
    String expected = wellboreA.getUid() + "," + wellboreB.getUid();
    String actual = this.valve.createObject(qc).get();
    assertEquals(expected, actual);
  }

  @Test
  void shouldDeleteSingleObject() throws Exception {
    // build witsmlObjects list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();
    ObjWellbores wmlObjWellbores = new ObjWellbores();

    ObjWellbore wellboreA = new ObjWellbore();
    wellboreA.setUid("wellbore-A");
    witsmlObjects.add(wellboreA);
    wmlObjWellbores.addWellbore(wellboreA);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "wellbore",
            null,
            WitsmlMarshal.serialize(wmlObjWellbores),
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldDeleteSingleObject" // exchange ID
            );

    // test getObject
    this.valve.deleteObject(qc);

    // verify
    verify(this.mockDelegator)
        .deleteObject(wellboreA, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient);
    verifyNoMoreInteractions(this.mockDelegator);
  }

  @Test
  void shouldDeleteLogRecurringElement() throws Exception {
    // build witsmlObjects lists
    ArrayList<AbstractWitsmlObject> logs1311 = new ArrayList<>();
    ArrayList<AbstractWitsmlObject> logs1411 = new ArrayList<>();

    ObjLogs logsObj1311 = new ObjLogs();
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs logsObj1411 =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs();

    // get traj 1311
    ObjLog log1311 = new ObjLog();
    log1311.setUid("HM_800023");
    log1311.setUidWell("U2");
    log1311.setUidWellbore("WBDD600");
    com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo csLogCurveInfo =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();
    csLogCurveInfo.setUid("lci-13");
    List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> csLogCurveInfoList =
        new ArrayList<>();
    csLogCurveInfoList.add(csLogCurveInfo);
    log1311.setLogCurveInfo(csLogCurveInfoList);
    logs1311.add(log1311);
    logsObj1311.addLog(log1311);

    // get traj 1411
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log1411 =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog();
    log1411.setUid("HM_800023");
    log1411.setUidWell("U2");
    log1411.setUidWellbore("WBDD600");
    csLogCurveInfo.setUid("lci-13");
    csLogCurveInfoList.add(csLogCurveInfo);
    log1411.setLogCurveInfo(csLogCurveInfoList);
    logs1411.add(log1411);
    logsObj1411.addLog(log1411);

    // build query contexts
    QueryContext qc1311 =
        new QueryContext(
            "1.3.1.1",
            "log",
            null,
            WitsmlMarshal.serialize(logsObj1311),
            logs1311,
            "goodUsername",
            "goodPassword",
            "shouldDeleteLogRecurringElement-1311" // exchange ID
            );
    QueryContext qc1411 =
        new QueryContext(
            "1.4.1.1",
            "log",
            null,
            WitsmlMarshal.serialize(logsObj1411),
            logs1411,
            "goodUsername",
            "goodPassword",
            "shouldDeleteLogRecurringElement-1411" // exchange ID
            );

    // test deletes
    this.valve.deleteObject(qc1311);
    this.valve.deleteObject(qc1411);

    // verify
    verify(this.mockDelegator)
        .deleteObject(
            log1311, qc1311.getUsername(), qc1311.getPassword(), qc1311.getExchangeId(), this.mockClient);
    verify(this.mockDelegator)
        .deleteObject(
            log1411, qc1411.getUsername(), qc1411.getPassword(), qc1411.getExchangeId(), this.mockClient);
    verifyNoMoreInteractions(this.mockDelegator);
  }

  @Test
  void shouldDeleteLogSubNode() throws Exception {
    // build witsmlObjects lists
    ArrayList<AbstractWitsmlObject> logs1311 = new ArrayList<>();
    ArrayList<AbstractWitsmlObject> logs1411 = new ArrayList<>();

    ObjLogs logsObj1311 = new ObjLogs();
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs logsObj1411 =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs();

    // get log 1311
    ObjLog log1311 = new ObjLog();
    log1311.setUid("HM_800023");
    log1311.setUidWell("U2");
    log1311.setUidWellbore("WBDD600");
    log1311.setServiceCompany(null);
    log1311.setRunNumber(null);
    logs1311.add(log1311);
    logsObj1311.addLog(log1311);

    // get log 1411
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log1411 =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog();
    log1411.setUid("HM_800023");
    log1411.setUidWell("U2");
    log1411.setUidWellbore("WBDD600");
    log1411.setServiceCompany(null);
    log1411.setRunNumber(null);
    logs1411.add(log1411);
    logsObj1411.addLog(log1411);

    // build query contexts
    QueryContext qc1311 =
        new QueryContext(
            "1.3.1.1",
            "log",
            null,
            WitsmlMarshal.serialize(logsObj1311),
            logs1311,
            "goodUsername",
            "goodPassword",
            "shouldDeleteLogSubNode-1311" // exchange ID
            );
    QueryContext qc1411 =
        new QueryContext(
            "1.4.1.1",
            "log",
            null,
            WitsmlMarshal.serialize(logsObj1411),
            logs1411,
            "goodUsername",
            "goodPassword",
            "shouldDeleteLogSubNode-1411" // exchange ID
            );

    // test deletes
    this.valve.deleteObject(qc1311);
    this.valve.deleteObject(qc1411);

    // verify
    verify(this.mockDelegator)
        .deleteObject(
            log1311, qc1311.getUsername(), qc1311.getPassword(), qc1311.getExchangeId(), this.mockClient);
    verify(this.mockDelegator)
        .deleteObject(
            log1411, qc1411.getUsername(), qc1411.getPassword(), qc1411.getExchangeId(), this.mockClient);
    verifyNoMoreInteractions(this.mockDelegator);
  }

  @Test
  void shouldDeleteTrajectory() throws Exception {
    // build witsmlObjects lists
    ArrayList<AbstractWitsmlObject> trajs1311 = new ArrayList<>();
    ArrayList<AbstractWitsmlObject> trajs1411 = new ArrayList<>();

    ObjTrajectorys trajsObj1311 = new ObjTrajectorys();
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys trajsObj1411 =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys();

    // get traj 1311
    ObjTrajectory traj1311 = new ObjTrajectory();
    traj1311.setUid("traj-1311");
    trajs1311.add(traj1311);
    trajsObj1311.addTrajectory(traj1311);

    // get traj 1411
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory traj1411 =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory();
    traj1411.setUid("traj-1411");
    trajs1411.add(traj1411);
    trajsObj1411.addTrajectory(traj1411);

    // build query contexts
    QueryContext qc1311 =
        new QueryContext(
            "1.3.1.1",
            "trajectory",
            null,
            WitsmlMarshal.serialize(trajsObj1311),
            trajs1311,
            "goodUsername",
            "goodPassword",
            "shouldDeleteTrajectory-1311" // exchange ID
            );
    QueryContext qc1411 =
        new QueryContext(
            "1.4.1.1",
            "trajectory",
            null,
            WitsmlMarshal.serialize(trajsObj1411),
            trajs1411,
            "goodUsername",
            "goodPassword",
            "shouldDeleteTrajectory-1411" // exchange ID
            );

    // test deletes
    this.valve.deleteObject(qc1311);
    this.valve.deleteObject(qc1411);

    // verify
    verify(this.mockDelegator)
        .deleteObject(
            traj1311, qc1311.getUsername(), qc1311.getPassword(), qc1311.getExchangeId(), this.mockClient);
    verify(this.mockDelegator)
        .deleteObject(
            traj1411, qc1411.getUsername(), qc1411.getPassword(), qc1411.getExchangeId(), this.mockClient);
    verifyNoMoreInteractions(this.mockDelegator);
  }

  @Test
  void shouldNotDeletePluralObjectAndThrowException() throws Exception {
    // build witsmlObjects list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();

    ObjWellbores wmlObjWellbores = new ObjWellbores();

    ObjWellbore wellboreA = new ObjWellbore();
    wellboreA.setUid("wellbore-A");
    witsmlObjects.add(wellboreA);
    wmlObjWellbores.addWellbore(wellboreA);

    ObjWellbore wellboreB = new ObjWellbore();
    wellboreB.setUid("wellbore-B");
    wmlObjWellbores.addWellbore(wellboreB);
    witsmlObjects.add(wellboreB);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "wellbore",
            null,
            WitsmlMarshal.serialize(wmlObjWellbores),
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldDeletePluralObject" // exchange ID
            );

    // test getObject
    assertThrows(ValveException.class, () -> this.valve.deleteObject(qc));

    // verify
    // verify(this.mockDelegator)
    //     .deleteObject(wellboreA, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient);
    // verify(this.mockDelegator)
    //     .deleteObject(wellboreB, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient);
    // verifyNoMoreInteractions(this.mockDelegator);
  }

  @Test
  void shouldUpdateObject() throws Exception {
    // build witsmlObjects list
    ArrayList<AbstractWitsmlObject> witsmlObjects;
    witsmlObjects = new ArrayList<>();

    ObjWellbore wellboreA = new ObjWellbore();
    wellboreA.setName("wellbore-A");
    wellboreA.setUid("wellbore-A");
    witsmlObjects.add(wellboreA);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.3.1.1",
            "wellbore",
            null,
            "",
            witsmlObjects,
            "goodUsername",
            "goodPassword",
            "shouldUpdateObject" // exchange ID
            );

    // test getObject
    this.valve.updateObject(qc);

    // verify
    verify(this.mockDelegator)
        .updateObject(wellboreA, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.mockClient);
    verifyNoMoreInteractions(this.mockDelegator);
  }

  @Test
  void shouldSearchTrajStationMdMn() throws Exception {
    // =============================================================================
    // Creation of search objects...
    // =============================================================================
    ObjTrajectory traj = new ObjTrajectory();
    traj.setUid("traj-search");
    // =============================================================================
    // Query logic test step -- tests just for the case where the object type is
    // trajectory, UID is provided, and one of the following query args exist:
    //		 (1) lastUpdateTimeUtc
    //		*(2) mdMn
    //		 (3) mdMx
    // (Note: All three query args may be present, but it only takes one to be
    //        present to query using a search; any combination of query args
    //        also is possible))
    // Case A -- mdMn = 500,  mdMx = null, lastUpdateTimeUtc does not have this key
    //                                                       but dtimTrajEnd is null
    // =============================================================================
    String query = traj.getXMLString("1.4.1.1");
    // javax.xml.bind.JAXBException
    ObjTrajectorys trajectorysQuery = WitsmlMarshal.deserialize(query, ObjTrajectorys.class);
    ObjTrajectory singularTrajectoryQuery = trajectorysQuery.getTrajectory().get(0);
    singularTrajectoryQuery.setName("traj-search");
    // Case A:
    // boolean expected = true;
    performTestCase(
        query, 500.0, null, singularTrajectoryQuery, "shouldSearchTrajectoryCaseA", true);
  }

  @Test
  void shouldSearchTrajStationMdMx() throws Exception {
    // =============================================================================
    // Creation of search objects...
    // =============================================================================
    ObjTrajectory traj = new ObjTrajectory();
    traj.setUid("traj-search");
    // =============================================================================
    // Query logic test step -- tests just for the case where the object type is
    // trajectory, UID is provided, and one of the following query args exist:
    //		 (1) lastUpdateTimeUtc
    //		 (2) mdMn
    //		*(3) mdMx
    // (Note: All three query args may be present, but it only takes one to be
    //        present to query using a search; any combination of query args
    //        also is possible))
    // Case B -- mdMn = null,  mdMx = 500, lastUpdateTimeUtc does not have this key
    //                                                       but dtimTrajEnd is null
    // =============================================================================
    String query = traj.getXMLString("1.4.1.1");
    ObjTrajectorys trajectorysQuery = WitsmlMarshal.deserialize(query, ObjTrajectorys.class);
    ObjTrajectory singularTrajectoryQuery = trajectorysQuery.getTrajectory().get(0);
    singularTrajectoryQuery.setName("traj-search");
    // Case B:
    performTestCase(
        query, null, 500.0, singularTrajectoryQuery, "shouldSearchTrajectoryCaseB", true);
  }

  @Test
  void shouldSearchTrajStationMdMnANDMdMx() throws Exception {
    // =============================================================================
    // Creation of search objects...
    // =============================================================================
    ObjTrajectory traj = new ObjTrajectory();
    traj.setUid("traj-search");
    // =============================================================================
    // Query logic test step -- tests just for the case where the object type is
    // trajectory, UID is provided, and one of the following query args exist:
    //		 (1) lastUpdateTimeUtc
    //		*(2) mdMn
    //		*(3) mdMx
    // (Note: All three query args may be present, but it only takes one to be
    //        present to query using a search; any combination of query args
    //        also is possible))
    // Case C -- mdMn = 100,  mdMx = 500,  lastUpdateTimeUtc does not have this key
    //                                                       but dtimTrajEnd is null
    // =============================================================================
    String query = traj.getXMLString("1.4.1.1");
    ObjTrajectorys trajectorysQuery = WitsmlMarshal.deserialize(query, ObjTrajectorys.class);
    ObjTrajectory singularTrajectoryQuery = trajectorysQuery.getTrajectory().get(0);
    singularTrajectoryQuery.setName("traj-search");
    // Case C:
    performTestCase(
        query, 100.0, 500.0, singularTrajectoryQuery, "shouldSearchTrajectoryCaseC", true);
  }

  // ================================================================================
  // PLEASE NOTE:
  // Case D -- Case A with lastUpdateTimeUtc not null <-- NOT IMPLEMENTED
  // Case E -- Case B with lastUpdateTimeUtc not null <-- NOT IMPLEMENTED
  // Case F -- Case C with lastUpdateTimeUtc not null <-- NOT IMPLEMENTED
  // ================================================================================

  @Test
  void shouldNotSearchTrajStation() throws Exception {
    // =============================================================================
    // Creation of search objects...
    // =============================================================================
    ObjTrajectory traj = new ObjTrajectory();
    traj.setUid("traj-search");
    // =============================================================================
    // Query logic test step -- tests just for the case where the object type is
    // trajectory, UID is provided, and one of the following query args exist:
    //		 (1) lastUpdateTimeUtc
    //		 (2) mdMn
    //		 (3) mdMx
    // (Note: All three query args may be present, but it only takes one to be
    //        present to query using a search; any combination of query args
    //        also is possible))
    // Case G -- mdMn = null,  mdMx = null,  lastUpdateTimeUtc does not have this key
    //                                                       but dtimTrajEnd is null
    // =============================================================================
    String query = traj.getXMLString("1.4.1.1");
    ObjTrajectorys trajectorysQuery = WitsmlMarshal.deserialize(query, ObjTrajectorys.class);
    ObjTrajectory singularTrajectoryQuery = trajectorysQuery.getTrajectory().get(0);
    singularTrajectoryQuery.setName("traj-search");
    // Case G:
    performTestCase(
        query, null, null, singularTrajectoryQuery, "shouldNotSearchTrajectoryCaseG", false);
  }

  public void performTestCase(
      String query,
      Double mdMn,
      Double mdMx,
      ObjTrajectory singularTrajectoryQuery,
      String caseName,
      boolean expectedValue) {

    // Requires separate MeasuredDepthCoord objects to work correctly...
    com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mDMnMeasuredDepth =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord();
    com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mDMxmeasuredDepth =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord();

    mDMnMeasuredDepth.setValue(mdMn);
    singularTrajectoryQuery.setMdMn(mDMnMeasuredDepth);
    mDMxmeasuredDepth.setValue(mdMx);
    singularTrajectoryQuery.setMdMx(mDMxmeasuredDepth);
    // OK will this test it right...cases a-c this is empty,
    // but cases d-f this is a value
    List<AbstractWitsmlObject> wmlObjectsQuery = new ArrayList<>();

    wmlObjectsQuery.add(singularTrajectoryQuery);

    // build query context
    QueryContext qc =
        new QueryContext(
            "1.4.1.1",
            "trajectory",
            null,
            query,
            wmlObjectsQuery,
            "goodUsername",
            "goodPassword",
            caseName);

    // let the software-under-test do its thing...
    assertEquals(expectedValue, DotValve.trajHasSearchQueryArgs(qc));
  }

  @Test
  void shouldUpdateTrajectory() throws Exception {
    // build witsmlObjects lists
    ArrayList<AbstractWitsmlObject> trajs1311 = new ArrayList<>();
    ArrayList<AbstractWitsmlObject> trajs1411 = new ArrayList<>();

    // get traj 1311
    ObjTrajectory traj1311 = new ObjTrajectory();
    traj1311.setName("traj-1311");
    traj1311.setUid("traj-1311");
    trajs1311.add(traj1311);

    // get traj 1411
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory traj1411 =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory();
    traj1411.setName("traj-1411");
    traj1411.setUid("traj-1411");
    trajs1411.add(traj1411);

    // build query contexts
    QueryContext qc1311 =
        new QueryContext(
            "1.3.1.1",
            "trajectory",
            null,
            "",
            trajs1311,
            "goodUsername",
            "goodPassword",
            "shouldUpdateTrajectory-1311" // exchange ID
            );
    QueryContext qc1411 =
        new QueryContext(
            "1.4.1.1",
            "trajectory",
            null,
            "",
            trajs1411,
            "goodUsername",
            "goodPassword",
            "shouldUpdateTrajectory-1411" // exchange ID
            );

    // test update
    this.valve.updateObject(qc1311);
    this.valve.updateObject(qc1411);

    // verify
    verify(this.mockDelegator)
        .updateObject(
            traj1311, qc1311.getUsername(), qc1311.getPassword(), qc1311.getExchangeId(), this.mockClient);
    verify(this.mockDelegator)
        .updateObject(
            traj1411, qc1411.getUsername(), qc1411.getPassword(), qc1411.getExchangeId(), this.mockClient);
    verifyNoMoreInteractions(this.mockDelegator);
  }

  @Test
  void shouldSucceedAuthenticate() throws Exception {
    // mock behavior
    when(this.mockClient.getJWT(eq("goodUsername"), eq("goodPassword"), anyString()))
        .thenReturn(
            JWT.decode( // using dummy token string from https://jwt.io/
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiR29vZCBUb2tlbiJ9.II4bNgtahHhV4jl7dgGn8CGjVxZWwBMZht-4LXeqB_Y"));

    // test
    this.valve.authenticate("goodUsername", "goodPassword");

    // verify
    verify(this.mockClient).getJWT(eq("goodUsername"), eq("goodPassword"), anyString());
    verifyNoMoreInteractions(this.mockClient);
  }

  @Test
  void shouldFailAuthenticate() throws ValveAuthException {
    // add mock behavior
    when(this.mockClient.getJWT("badUsername", "badPassword", "n/a"))
        .thenThrow(ValveAuthException.class);

    // test
    assertThrows(
        ValveAuthException.class,
        () -> this.valve.authenticate("badUsername", "badPassword"));
  }

  @Test
  void shouldGetCap() {
    // get cap
    Map<String, AbstractWitsmlObject[]> cap = this.valve.getCap();

    // verify keys
    assertFalse(cap.isEmpty());
    assertTrue(cap.containsKey("WMLS_AddToStore"));
    assertTrue(cap.containsKey("WMLS_GetFromStore"));
    assertTrue(cap.containsKey("WMLS_DeleteFromStore"));
    assertTrue(cap.containsKey("WMLS_UpdateInStore"));

    // get values
    AbstractWitsmlObject[] actualAddObjects = cap.get("WMLS_AddToStore");
    AbstractWitsmlObject[] actualGetObjects = cap.get("WMLS_GetFromStore");
    AbstractWitsmlObject[] actualDeleteObjects = cap.get("WMLS_DeleteFromStore");
    AbstractWitsmlObject[] actualUpdateObjects = cap.get("WMLS_UpdateInStore");

    // verify values
    assertEquals("well", actualAddObjects[0].getObjectType());
    assertEquals("wellbore", actualAddObjects[1].getObjectType());
    assertEquals("trajectory", actualAddObjects[2].getObjectType());
    assertEquals("well", actualGetObjects[0].getObjectType());
    assertEquals("wellbore", actualGetObjects[1].getObjectType());
    assertEquals("well", actualDeleteObjects[0].getObjectType());
    assertEquals("wellbore", actualDeleteObjects[1].getObjectType());
    assertEquals("trajectory", actualDeleteObjects[2].getObjectType());
    assertEquals("well", actualUpdateObjects[0].getObjectType());
    assertEquals("wellbore", actualUpdateObjects[1].getObjectType());
    assertEquals("trajectory", actualUpdateObjects[2].getObjectType());
  }
}
