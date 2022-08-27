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

package com.hashmapinc.tempus.witsml.server.api;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.WitsmlObjectParser;
import com.hashmapinc.tempus.witsml.WitsmlUtil;
import com.hashmapinc.tempus.witsml.server.WitsmlApiConfig;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsAddToStoreResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsDeleteFromStoreResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsGetBaseMsgResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsGetCapResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsGetFromStoreResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsGetVersionResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsUpdateInStoreResponse;
import com.hashmapinc.tempus.witsml.server.api.model.cap.DataObject;
import com.hashmapinc.tempus.witsml.server.api.model.cap.ServerCap;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.ValveFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.feature.Features;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@WebService(
    serviceName = "WMLS",
    portName = "StoreSoapPort",
    targetNamespace = "http://www.witsml.org/wsdl/120",
    endpointInterface = "com.hashmapinc.tempus.witsml.server.api.IStore")
@Features(features = "com.hashmapinc.tempus.witsml.server.api.interceptors.PrettyLoggingFeature")
public class StoreImpl implements IStore {

  private ServerCap cap;
  private WitsmlApiConfig witsmlApiConfigUtil;
  private IValve valve;
  private ValveConfig config;

  @Value("${wmls.version}")
  private String version;

  @Value("${valve.name}")
  private String valveName;

  @Autowired
  private void setServerCap(ServerCap cap) {
    this.cap = cap;
  }

  @Autowired
  private void setWitsmlApiConfig(WitsmlApiConfig witsmlApiConfigUtil) {
    this.witsmlApiConfigUtil = witsmlApiConfigUtil;
  }

  @Autowired
  private void setValveConfig(ValveConfig config) {
    this.config = config;
  }

  @PostConstruct
  private void setValve() {
    // get the valve
    try {
      valve = ValveFactory.buildValve(valveName, config.getConfiguration());
    } catch (ValveAuthException e) {
      LOGGER.info("Error creating the valve: " + e.getMessage());
    }

    // =====================================================================
    // update the cap with this valve's capabililies
    // =====================================================================
    // get the valve capabilities
    Map<String, AbstractWitsmlObject[]> valveCaps = valve.getCap();
    LOGGER.info(
        ValveLogging.getLogMsg(
            "Received the following capabilities from valve: " + valveCaps.toString()));

    // populate the cap object from the valveCaps
    for (Map.Entry<String, AbstractWitsmlObject[]> entry : valveCaps.entrySet()) {
      // get list of data objects
      List<DataObject> supportedDataObjects =
          Stream.of(entry.getValue())
              .map(
                  awo -> {
                    DataObject dataObject = new DataObject();
                    dataObject.setName(awo.getObjectType());
                    return dataObject;
                  })
              .collect(Collectors.toList());

      // add function to cap
      this.cap.addFunction(entry.getKey(), supportedDataObjects);
    }
    // =====================================================================
  }

  private String getExchangeId() {
    Message message = PhaseInterceptorChain.getCurrentMessage();
    if (message == null) {
      return "99999999-9999-9999-9999-999999999999"; // TODO: is this smart? (I don't know it isn't,
                                                     // just checking)
    }
    return message.getExchange().get(LogEvent.KEY_EXCHANGE_ID).toString();
  }

  @Override
  public WmlsAddToStoreResponse addToStore(
      String wmlTypeIn, String xmlIn, String optionsIn, String capabilitiesIn) {

    LOGGER.debug(ValveLogging.getLogMsg(getExchangeId(), "Executing addToStore for query"));
    // try to add to store
    List<AbstractWitsmlObject> witsmlObjects;
    String uid;
    WmlsAddToStoreResponse response = new WmlsAddToStoreResponse();
    try {
      // build the query context
      Map<String, String> optionsMap = WitsmlUtil.parseOptionsIn(optionsIn);
      short validationResult = StoreValidator.validateAddToStore(wmlTypeIn, xmlIn, valve);
      if (validationResult != 1) {
        response.setResult(validationResult);
        return response;
      }
      String versionFromXml = WitsmlUtil.getVersionFromXML(xmlIn);
      // create the correct Object model (e.g. ObjFluidsReport) for the version from the raw XML
      witsmlObjects = WitsmlObjectParser.parse(wmlTypeIn, xmlIn, versionFromXml);
      ValveUser user =
          (ValveUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      QueryContext qc =
          new QueryContext(
              versionFromXml,
              wmlTypeIn,
              optionsMap,
              xmlIn,
              witsmlObjects,
              user.getUserName(),
              user.getPassword(),
              getExchangeId());
      // handle each object asynchronously
      uid = valve.createObject(qc).get();
    } catch (ValveException ve) {
      // TODO: handle exception
      LOGGER.warn(ValveLogging.getLogMsg(getExchangeId(), "ValveException in addToStore: " + ve));
      if (ve.getErrorCode() != -1) {
        response.setSuppMsgOut(
            witsmlApiConfigUtil.getProperty("basemessages." + ve.getErrorCode()));
        response.setResult(ve.getErrorCode());
      } else {
        response.setSuppMsgOut(ve.getMessage());
        response.setResult(ve.getErrorCode());
      }
      return response;
    } catch (Exception e) {
      // TODO: handle exception
      LOGGER.warn(
          ValveLogging.getLogMsg(
              getExchangeId(), "Could not add WITSML object to store: \n" + "Error: " + e));
      response.setSuppMsgOut("Error adding to store: " + e.getMessage());
      response.setResult((short) -1);

      return response;
    }

    response.setSuppMsgOut(uid);
    response.setResult((short) 1);

    return response;
  }

  @Override
  public WmlsUpdateInStoreResponse updateInStore(
      String wmlTypeIn, String xmlIn, String optionsIn, String capabilitiesIn) {
    LOGGER.debug(ValveLogging.getLogMsg(getExchangeId(), "Executing updateInStore"));
    // try to update in store
    List<AbstractWitsmlObject> witsmlObjects;
    WmlsUpdateInStoreResponse response = new WmlsUpdateInStoreResponse();
    try {
      // build the query context
      Map<String, String> optionsMap = WitsmlUtil.parseOptionsIn(optionsIn);
      short validationResult =
          StoreValidator.validateUpdateInStore(wmlTypeIn, xmlIn, optionsMap, valve);
      if (validationResult != 1) {
        response.setResult(validationResult);
        response.setSuppMsgOut(
            witsmlApiConfigUtil.getProperty("basemessages." + response.getResult()));
        return response;
      }
      String versionFromXml = WitsmlUtil.getVersionFromXML(xmlIn);
      // create the correct Object model (e.g. ObjFluidsReport) for the version from the raw XML
      witsmlObjects = WitsmlObjectParser.parse(wmlTypeIn, xmlIn, versionFromXml);
      ValveUser user =
          (ValveUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      QueryContext qc =
          new QueryContext(
              versionFromXml,
              wmlTypeIn,
              optionsMap,
              xmlIn,
              witsmlObjects,
              user.getUserName(),
              user.getPassword(),
              getExchangeId());

      // perform update
      valve.updateObject(qc).get();
    } catch (ValveException ve) {
      // TODO: handle exception
      LOGGER.warn(
          ValveLogging.getLogMsg(
              getExchangeId(), "Valve Exception in updateInStore: " + ve.getMessage()));
      response.setSuppMsgOut(ve.getMessage());
      response.setResult((short) -1);
      return response;
    } catch (Exception e) {
      // TODO: handle exception
      LOGGER.warn(
          ValveLogging.getLogMsg(
              getExchangeId(), "Could not add WITSML object to store: \n" + "Error: " + e));
      response.setSuppMsgOut("Error updating in store: " + e.getMessage());
      response.setResult((short) -1);
      return response;
    }

    response.setResult((short) 1);
    response.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + response.getResult()));
    return response;
  }

  @Override
  public WmlsDeleteFromStoreResponse deleteFromStore(
      String wmlTypeIn, String queryIn, String optionsIn, String capabilitiesIn) {
    LOGGER.debug(ValveLogging.getLogMsg(getExchangeId(), "Deleting object from store."));
    WmlsDeleteFromStoreResponse resp = new WmlsDeleteFromStoreResponse();
    // set initial ERROR state for resp
    resp.setResult((short) -1);

    Map<String, String> optionsMap = WitsmlUtil.parseOptionsIn(optionsIn);
    short validationResult =
        StoreValidator.validateDeleteFromStore(wmlTypeIn, queryIn, optionsMap, valve);
    if (validationResult != 1) {
      resp.setResult(validationResult);
      resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
      return resp;
    }
    // try to deserialize
    List<AbstractWitsmlObject> witsmlObjects;
    try {
      String clientVersion = WitsmlUtil.getVersionFromXML(queryIn);
      // create the correct Object model (e.g. ObjFluidsReport) for the version from the raw XML
      witsmlObjects = WitsmlObjectParser.parse(wmlTypeIn, queryIn, clientVersion);
    } catch (Exception e) {
      // TODO: handle exception
      LOGGER.warn(
          ValveLogging.getLogMsg(
              getExchangeId(),
              "could not deserialize witsml object: \n"
                  + "WMLtypeIn: " + wmlTypeIn + " \n"
                  + "QueryIn: " + queryIn + " \n"
                  + "OptionsIn: " + optionsIn + " \n"
                  + "CapabilitiesIn: " + capabilitiesIn));
      resp.setSuppMsgOut("Bad QueryIn. Got error message: " + e.getMessage());
      return resp;
    }

    // try to delete
    try {
      // construct query context

      ValveUser user =
          (ValveUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      QueryContext qc =
          new QueryContext(
              null, // client version not needed
              wmlTypeIn,
              optionsMap,
              queryIn,
              witsmlObjects,
              user.getUserName(),
              user.getPassword(),
              getExchangeId());
      this.valve.deleteObject(qc).get();
      resp.setResult((short) 1);
    } catch (ValveException e) {
      resp.setSuppMsgOut(e.getMessage());
      if (e.getErrorCode() != null) {
        resp.setResult(e.getErrorCode());
        resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + e.getErrorCode()));
      }
      return resp;
    } catch (Exception ex) {
      resp.setSuppMsgOut(ex.getMessage());
      resp.setResult((short) -1);
      return resp;
    }

    resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
    return resp;
  }

  @Override
  public WmlsGetVersionResponse getVersion() {
    LOGGER.debug(ValveLogging.getLogMsg(getExchangeId(), "Executing GetVersion"));
    WmlsGetVersionResponse resp = new WmlsGetVersionResponse();
    resp.setResult(version);
    return resp;
  }

  @Override
  public WmlsGetCapResponse getCap(String optionsIn) {
    LOGGER.debug(ValveLogging.getLogMsg(getExchangeId(), "Executing GetCap"));
    WmlsGetCapResponse resp = new WmlsGetCapResponse();
    short validationResult = StoreValidator.validateGetCap(optionsIn);
    if (validationResult != 1) {
      resp.setResult(validationResult);
      resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
      return resp;
    }
    Map<String, String> options = WitsmlUtil.parseOptionsIn(optionsIn);
    String requestedVersion = options.get("dataVersion");

    resp.setSuppMsgOut("");
    try {
      // get cap string and populate response data
      String data = cap.getWitsmlObject(requestedVersion);
      LOGGER.debug(ValveLogging.getLogMsg(getExchangeId(), "Returning cap: " + data));
      resp.setCapabilitiesOut(data);
      resp.setResult((short) 1);
    } catch (UnsupportedOperationException e) {
      resp.setResult((short) -424);
      LOGGER.warn(
          ValveLogging.getLogMsg(
              getExchangeId(), "Unsupported version requested: " + e.getMessage()));
    } catch (JAXBException e) {
      resp.setResult((short) -1001);
      resp.setSuppMsgOut(
          "Unable to generate the capabilities object due to misconfiguration of the server");
      LOGGER.warn(
          ValveLogging.getLogMsg(
              getExchangeId(),
              "Unable to generate the capabilities object due to misconfiguration of the server: "
                  + e.getMessage()));
    }
    resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
    return resp;
  }

  @Override
  public WmlsGetBaseMsgResponse getBaseMsg(Short returnValueIn) {
    LOGGER.debug(ValveLogging.getLogMsg(getExchangeId(), "Executing GetBaseMsg"));

    String errMsg = witsmlApiConfigUtil.getProperty("basemessages." + returnValueIn);
    if (errMsg == null) {
      errMsg = witsmlApiConfigUtil.getProperty("basemessages.-999");
    }

    WmlsGetBaseMsgResponse response = new WmlsGetBaseMsgResponse();
    response.setResult(errMsg);

    return response;
  }

  @Override
  public WmlsGetFromStoreResponse getFromStore(
      String wmlTypeIn, String queryIn, String optionsIn, String capabilitiesIn) {
    WmlsGetFromStoreResponse resp = new WmlsGetFromStoreResponse();
    // try to deserialize
    Map<String, String> optionsMap = WitsmlUtil.parseOptionsIn(optionsIn);
    if (!optionsMap.containsKey("returnElements")) optionsMap.put("returnElements", "requested");
    // validates to make sure conditions are met
    short validationResult =
        StoreValidator.validateGetFromStore(wmlTypeIn, queryIn, optionsMap, valve);
    if (validationResult != 1) {
      resp.setResult(validationResult);
      resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
      return resp;
    }

    String xmlOut = null;
    // check for the presence of the requestObjectSelectionCapability option --
    // it is not necessary to retrieve data for a query since the query is a
    // predefined constant that returns the capabilities of the server due to type
    if (optionsMap.containsKey("requestObjectSelectionCapability")
        && optionsMap.get("requestObjectSelectionCapability").equals("true")) {
      xmlOut = valve.getObjectSelectionCapability(wmlTypeIn);
    } else {
      if (optionsMap.containsKey("requestObjectSelectionCapability")
          && !optionsMap.get("requestObjectSelectionCapability").equals("none")) {
        resp.setResult((short) -427);
        resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
        return resp;
      }
      if (optionsIn.contains("requestObjectSelectionCapability")
          && !optionsMap.containsKey("requestObjectSelectionCapability")) {
        // value of the key must have been null since the parse never placed the key/value pair
        // into the map
        resp.setResult((short) -411);
        resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
        return resp;
      }
      List<AbstractWitsmlObject> witsmlObjects;
      String clientVersion;
      try {
        clientVersion = WitsmlUtil.getVersionFromXML(queryIn);
        // create the correct Object model (e.g. ObjFluidsReport) for the version from the raw XML
        witsmlObjects = WitsmlObjectParser.parse(wmlTypeIn, queryIn, clientVersion);
      } catch (Exception e) {
        // TODO: handle exception
        LOGGER.warn(
            ValveLogging.getLogMsg(
                getExchangeId(),
                "Could not deserialize witsml object: \n"
                    + "WMLtypeIn: " + wmlTypeIn + " \n"
                    + "QueryIn: " + queryIn + " \n"
                    + "OptionsIn: " + optionsIn + " \n"
                    + "CapabilitiesIn: " + capabilitiesIn));

        resp.setSuppMsgOut("Error parsing input: " + e.getMessage());
        resp.setResult((short) -1);
        return resp;
      }

      QueryContext qc = null;
      // try to query
      try {
        // construct query context
        ValveUser user =
            (ValveUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        qc =
            new QueryContext(
                clientVersion,
                wmlTypeIn,
                optionsMap,
                queryIn,
                witsmlObjects,
                user.getUserName(),
                user.getPassword(),
                getExchangeId());

        xmlOut = this.valve.getObject(qc).get();
        // convert to WITSML XML
        // LogConverterExtended logConverter = new LogConverterExtended();
        /*
        try {
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlXmlOut =
                    logConverter.convertTo1411(xmlOut);
        } catch (JAXBException jaxBEx) {
            throw new Exception("JAXB failure trying to generate GetFromStore response: " +
                    jaxBEx.getMessage());
        }
        */
        // TODO xmlOut is a String; need to go from an ObjLog v1411 to a String & put it into xmlOut

      } catch (ValveException ve) {
        resp.setResult((short) -425);
        if (qc != null)
          LOGGER.warn(
              ValveLogging.getLogMsg(
                  qc.EXCHANGE_ID, "Valve Exception in GetFromStore: " + ve.getMessage()));
        else
          LOGGER.warn(
              ValveLogging.getLogMsg("Valve Exception in GetFromStore: " + ve.getMessage()));
        resp.setSuppMsgOut(ve.getMessage());
      } catch (Exception e) {
        resp.setResult((short) -425);
        LOGGER.warn(ValveLogging.getLogMsg("Valve Exception in GetFromStore: " + e.getMessage()));
      }
    }

    // populate response
    if (null != xmlOut) {
      resp.setSuppMsgOut("");
      resp.setResult((short) 1);
      resp.setXmlOut(xmlOut);
    } else {
      resp.setSuppMsgOut("No Data Found.");
      resp.setResult((short) 1);
    }

    // return response
    if (resp.getSuppMsgOut().isEmpty())
      resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));

    return resp;
  }
}
