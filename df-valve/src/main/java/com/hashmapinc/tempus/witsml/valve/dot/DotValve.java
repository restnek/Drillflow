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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ObjectSelectionConstants;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@RequiredArgsConstructor
public class DotValve implements IValve {
  private final DotClient client;
  private final DotDelegator delegator;

  /**
   * Constructor that accepts a config map and builds the client and delegator fields
   */
  public DotValve(Map<String, String> config) throws ValveAuthException {
    String tokenPath = config.get("token.path");

    this.client = new DotClient(tokenPath);
    this.delegator = new DotDelegator(config);

    LOGGER.info(ValveLogging.getLogMsg("Creating valve pointing to url: " + tokenPath));
  }

  /**
   * Retrieve the name of the valve
   *
   * @return The name of the valve
   */
  @Override
  public String getName() {
    // DoT = Drillops Town
    return "DoT";
  }

  /**
   * Retrieve the description of the valve
   *
   * @return The description of the valve
   */
  @Override
  public String getDescription() {
    // DoT = Drillops Town
    return "Valve for interaction with Drillops Town";
  }

  /**
   * Gets the object based on the query from the WITSML STORE API
   *
   * @param wmlTypeIn - The WITSML type in from the API query
   * @return The resultant object from the query in xml string format NULL if invalid (unsupported)
   *     WITSML type
   */
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

  /**
   * Gets the object based on the query from the WITSML STORE API
   *
   * @param qc - QueryContext needed to execute the getObject querying
   * @return The resultant object from the query in xml string format
   */
  @Override
  @Async("asyncCustomTaskExecutor")
  public CompletableFuture<String> getObject(QueryContext qc) throws ValveException {
    // get query responses
    List<AbstractWitsmlObject> queryResponses;

    // IF  this is a singular object AND a UID is NOT provided ...
    if (qc.getWitsmlObjects().size() == 1
        && (qc.getWitsmlObjects().get(0).getUid() == null
            || "".equals(qc.getWitsmlObjects().get(0).getUid()))) {
      // ... query using a search
      queryResponses = doSearch(qc);
    } else {
      if ("trajectory".equals(qc.getObjectType()) && trajHasSearchQueryArgs(qc)) {
        // ... query using a search
        queryResponses = doSearch(qc);
      } else {
        // ... query using REST get
        queryResponses = getSingularObject(qc);
      }
    }

    // build xml response from query response
    String xmlResponse =
        DotTranslator.consolidateObjectsToXML(
            queryResponses, qc.getClientVersion(), qc.getObjectType());

    // return xml
    return CompletableFuture.completedFuture(xmlResponse);
  }

  /**
   * A trajectory object type that has a UID will query using a search if one of the following query
   * args exist: (1) lastUpdateTimeUtc (2) mdMn (3) mdMx All three may be present, but it only takes
   * one to be present to query using a search.
   */
  public static boolean trajHasSearchQueryArgs(QueryContext qc) {
    // Trajectory object must have a UID.
    if (qc.getWitsmlObjects().get(0).getUid() == null
        || "".equals(qc.getWitsmlObjects().get(0).getUid()))
      return false;

    // Get AbstractWitsmlObject to get at trajectory query args...
    AbstractWitsmlObject wmlObject = qc.getWitsmlObjects().get(0);

    // Needed for some query args since some fields are lost in 2.0
    String jsonString1411 = wmlObject.getJSONString("1.4.1.1");
    String jsonString20 = wmlObject.getJSONString("2.0");
    JSONObject trajectoryJson = new JSONObject(jsonString20); // 2.0
    JSONObject objTrajectoryJson = new JSONObject(jsonString1411);

    // Just need any one of these to be present to change the trajectory query from REST to
    // search...
    if (trajectoryJson.has("lastUpdateTimeUtc")
        && !JsonUtil.isEmpty(trajectoryJson.get("lastUpdateTimeUtc"))) return true;
    if (objTrajectoryJson.has("mdMn") && !JsonUtil.isEmpty(objTrajectoryJson.get("mdMn")))
      return true;
	  return objTrajectoryJson.has("mdMx") && !JsonUtil.isEmpty(objTrajectoryJson.get("mdMx"));
  }

  /**
   * This method gets a singular object by ID and returns the xml string of that object in the
   * proper client version format from qc.CLIENT_VERSION
   *
   * @param qc - query context for getting singular object
   * @return - array list of abstract witsml object responses
   */
  private ArrayList<AbstractWitsmlObject> getSingularObject(QueryContext qc) throws ValveException {
    // handle each object
    ArrayList<AbstractWitsmlObject> queryResponses = new ArrayList<>();
    try {
      for (AbstractWitsmlObject witsmlObject : qc.getWitsmlObjects()) {
        AbstractWitsmlObject response =
            this.delegator.getObject(
                witsmlObject,
                qc.getUsername(),
                qc.getPassword(),
                qc.getExchangeId(),
                this.client,
                qc.getOptionsIn());
        if (null != response) queryResponses.add(response);
      }
    } catch (Exception e) {
      LOGGER.warn(
          ValveLogging.getLogMsg(
              qc.getExchangeId(), "Exception in DotValve getSingularObject: " + e.getMessage()));
      throw new ValveException(e.getMessage());
    }

    // response
    return queryResponses;
  }

  /**
   * This function queries for objects by non-uid fields.
   *
   * @param qc - query context to use for searching
   * @return - array list of abstract witsml object responses
   */
  private List<AbstractWitsmlObject> doSearch(QueryContext qc) throws ValveException {
    // handle each object
    if (qc.getWitsmlObjects().size() > 1)
      LOGGER.info(
          ValveLogging.getLogMsg(
              qc.getExchangeId(), "Query received with more than one singular object, not supported"));
    // TODO: Throw exception here
    // handle search
    List<AbstractWitsmlObject> queryResponses;
    try {
      queryResponses =
          this.delegator.search(
              qc.getWitsmlObjects().get(0),
              qc.getUsername(),
              qc.getPassword(),
              qc.getExchangeId(),
              this.client,
              qc.getOptionsIn());
    } catch (Exception e) {
      LOGGER.warn(
          ValveLogging.getLogMsg(
              qc.getExchangeId(), "Exception in DotValve doSearch: " + e.getMessage()));
      throw new ValveException(e.getMessage());
    }

    // return consolidated XML response in proper version
    return queryResponses;
  }

  /**
   * Creates an object
   *
   * @param qc - query context to use for query execution
   * @return the UID of the newly created object
   */
  @Override
  @Async("asyncCustomTaskExecutor")
  public CompletableFuture<String> createObject(QueryContext qc) throws ValveException {
    // create each object
    ArrayList<String> uids = new ArrayList<>();
    LOGGER.debug(ValveLogging.getLogMsg(qc.getExchangeId(), "Async create object"));

    try {
      for (AbstractWitsmlObject witsmlObject : qc.getWitsmlObjects()) {
        uids.add(
            this.delegator.createObject(
                witsmlObject, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.client));
      }

    } catch (ValveException e) {
      LOGGER.warn(
          ValveLogging.getLogMsg(
              qc.getExchangeId(), "Exception in DotValve createObject: " + e.getMessage()));
      throw new ValveException(e.getMessage(), e.getErrorCode());
    } catch (Exception e) {
      LOGGER.warn(
          ValveLogging.getLogMsg(
              qc.getExchangeId(), "Exception in DotValve createObject: " + e.getMessage()));
      throw new ValveException(e.getMessage());
    }
    // return the uids created, comma separated
    return CompletableFuture.completedFuture(StringUtils.join(uids, ','));
  }

  /**
   * Deletes an object
   *
   * @param qc - QueryContext with information needed to delete object
   */
  @Override
  @Async("asyncCustomTaskExecutor")
  public CompletableFuture<Boolean> deleteObject(QueryContext qc) throws ValveException {
    // delete each object with 1 retry for bad token errors
    boolean result;
    // According to the WITSML Spec you cannot delete more than one object (or sub elements of more
    // than one object)
    // at a time, so we do this check
    if (qc.getWitsmlObjects().size() > 1)
      throw new ValveException(
          "Delete cannot have more than one singular object per query", (short) -444);
    // This is rare but a check must be made
    if (qc.getWitsmlObjects().isEmpty())
      throw new ValveException(
          "Delete must have exactly one singluar object, but found 0 in the query");
    try {
      // Should be a safe assumption as we check above
      AbstractWitsmlObject wmlObject = qc.getWitsmlObjects().get(0);

      if (wmlObject.getUid() == null || "".equals(wmlObject.getUid())) {
        throw new ValveException(
            "Delete cannot have more than one singular object per query", (short) -415);
      }

      if (!isObjectDelete(wmlObject.getObjectType().toLowerCase(), qc.getQueryXml().toLowerCase())) {
        // This is an element delete so re-route to delegator update
        this.delegator.performElementDelete(
            wmlObject, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.client);
      } else {
        // This is an object delete, so straight delete
        this.delegator.deleteObject(
            wmlObject, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.client);
      }
      result = true;
    } catch (Exception e) {
      LOGGER.warn(
          ValveLogging.getLogMsg(
              qc.getExchangeId(), "Exception in DotValve deleteObject: " + e.getMessage()));
      if (e.getClass() == ValveException.class) throw ((ValveException) e);
      throw new ValveException(e.getMessage());
    }
    return CompletableFuture.completedFuture(result);
  }

  /**
   * Determines if this delete is an element delete or object delete
   *
   * @param dataObjectType they type of object to search for
   * @param data the WITSML query template document
   * @return True if it is an object delete, false if it is an element delete
   */
  private boolean isObjectDelete(String dataObjectType, String data) {
    // This regex matches (for dataObjectType: well) <well uid="xxx" /> and <well uid="xxx"></well>
    // and is multiline capable
    Pattern singularPattern =
        Pattern.compile(
            "(<["
                + dataObjectType
                + "][^<]*?/>)|<(["
                + dataObjectType
                + "][^<]*?>*[\\s\\S]</"
                + dataObjectType
                + ">)");
    Matcher m = singularPattern.matcher(data);
    return m.find();
  }

  /**
   * Updates an already existing object
   *
   * @param qc - QueryContext needed to execute the deleteObject querying
   */
  @Override
  @Async("asyncCustomTaskExecutor")
  public CompletableFuture<Boolean> updateObject(QueryContext qc) throws ValveException {
    // update each object with 1 retry for bad tokens
    boolean result;
    try {
      for (AbstractWitsmlObject witsmlObject : qc.getWitsmlObjects()) {
        this.delegator.updateObject(
            witsmlObject, qc.getUsername(), qc.getPassword(), qc.getExchangeId(), this.client);
      }
      result = true;
    } catch (Exception e) {
      LOGGER.warn(
          ValveLogging.getLogMsg(
              qc.getExchangeId(), "Exception in DotValve updateObject: " + e.getMessage()));
      throw new ValveException(e.getMessage());
    }
    return CompletableFuture.completedFuture(result);
  }

  /**
   * Authenticates with the DotAuth class
   *
   * @param username The username to authenticate with
   * @param password The password to authenticate with
   */
  @Override
  public void authenticate(String username, String password) throws ValveAuthException {
    this.client.getJWT(username, password, "n/a");
  }

  /**
   * Return a map of FUNCTION_NAME->LIST_OF_SUPPORTED_OBJECTS
   *
   * @return capabilities - map of FUNCTION_NAME->LIST_OF_SUPPORTED_OBJECTS
   */
  public Map<String, AbstractWitsmlObject[]> getCap() {
    // define capabilities map
    Map<String, AbstractWitsmlObject[]> cap = new HashMap<>();

    // array of supported functions
    String[] funcs = {
      "WMLS_AddToStore", "WMLS_GetFromStore", "WMLS_DeleteFromStore", "WMLS_UpdateInStore"
    };

    // supported objects for each function
    // choice of 1311 is arbitrary
    AbstractWitsmlObject well = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell();
    // choice of 1311 is arbitrary
    AbstractWitsmlObject wellbore = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore();
    // choice of 1311 is arbitrary
    AbstractWitsmlObject trajectory = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory();
    // choice of 1311 is arbitrary
    AbstractWitsmlObject log = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog();
    // choice of 1311 is arbitrary
    AbstractWitsmlObject fluidsReport =
        new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport();
    AbstractWitsmlObject[][] supportedObjects = {
      {well, wellbore, trajectory, log, fluidsReport}, // ADD TO STORE OBJECTS
      {well, wellbore, trajectory, log, fluidsReport}, // GET FROM STORE OBJECTS
      {well, wellbore, trajectory, log, fluidsReport}, // DELETE FROM STORE OBJECTS
      {well, wellbore, trajectory, log, fluidsReport}, // UPDATE IN STORE OBJECTS
    };

    // populate cap
    for (int i = 0; i < funcs.length; i++) {
      cap.put(funcs[i], supportedObjects[i]);
    }

    return cap;
  }
}
