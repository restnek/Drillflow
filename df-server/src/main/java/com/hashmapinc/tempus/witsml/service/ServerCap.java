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

package com.hashmapinc.tempus.witsml.service;

import com.hashmapinc.tempus.witsml.configuration.properties.ServerCapProperties;
import com.hashmapinc.tempus.witsml.model.cap.v1411.ObjCapServer;
import com.hashmapinc.tempus.witsml.model.cap.v1411.ObjectFactory;
import com.hashmapinc.tempus.witsml.model.cap.DataObject;
import com.hashmapinc.tempus.witsml.model.cap.v1311.CsContact;
import com.hashmapinc.tempus.witsml.model.cap.v1311.CsFunction;
import com.hashmapinc.tempus.witsml.model.cap.v1311.ObjCapServers;
import com.hashmapinc.tempus.witsml.model.cap.v1411.GrowingTimeoutPeriod;
import com.hashmapinc.tempus.witsml.model.cap.v1411.ObjectWithConstraint;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

@Service
public class ServerCap {

  private final ServerCapProperties serverCapProperties;

  private String serverName;
  private String vendor;

  // Functions and details
  private final Map<String, Integer> growingTimeouts = new HashMap<>();
  private final Map<String, List<DataObject>> functions = new HashMap<>();

  public ServerCap(ServerCapProperties serverCapProperties) {
    this.addFunction("WMLS_GetBaseMsg", null);
    this.addFunction("WMLS_GetVersion", null);
    DataObject object = new DataObject();
    object.setName("capServer");
    List<DataObject> objects = new ArrayList<>();
    objects.add(object);
    this.addFunction("WMLS_GetCap", objects);
    this.serverCapProperties = serverCapProperties;
  }

  /**
   * Add a Growing timeout period for a specific object (note: only applicable for 1.4.1.1)
   *
   * @param objectName The name of the object for the timeout
   * @param timeout The timeout for the object
   */
  public void addGrowingTimeoutPeriod(String objectName, int timeout) {
    growingTimeouts.put(objectName, timeout);
  }

  /**
   * Add a Growing timeout period for a specific object (note: only applicable for 1.4.1.1) Note: If
   * a timeout for the object exists, it will be removed, otherwise nothing will be done.
   *
   * @param objectName The name of the object to remove the timeout for
   */
  public void removeGrowingTimeoutPeriod(String objectName) {
    growingTimeouts.remove(objectName);
  }

  /**
   * Adds a function to the server capabilities
   *
   * @param functionName The name of the method (for example: "WMLS_GetFromStore")
   * @param objects A List of Data Objects that are supported for the function
   */
  public void addFunction(String functionName, List<DataObject> objects) {
    functions.put(functionName, objects);
  }

  /**
   * Returns the WITSML object in the version specified
   *
   * @param version The version of the XML that should be returned
   * @return The XML as a string
   * @throws UnsupportedOperationException If the version is not supported
   */
  public String getWitsmlObject(String version)
      throws JAXBException, UnsupportedOperationException {

    if (!"1.3.1.1".equals(version) && !"1.4.1.1".equals(version)) {
      throw new UnsupportedOperationException("Version not supported by the capabilites object");
    }
    if ("1.3.1.1".equals(version)) {
      return get1311Object();
    } else {
      return get1411Object();
    }
  }

  /**
   * Creates the 1.3.1.1 XML version of the capabilities object.
   *
   * @return A string representing the 1.3.1.1 XML version of the capabilities object.
   * @throws JAXBException An error occurred in marshalling the object to XML
   */
  private String get1311Object() throws JAXBException {
    // Create root
    ObjCapServers servers = new ObjCapServers();
    servers.setVersion("1.3.1");

    // Create server
    com.hashmapinc.tempus.witsml.model.cap.v1311.ObjCapServer
        server = new com.hashmapinc.tempus.witsml.model.cap.v1311.ObjCapServer();
    server.setApiVers("1.3.1");

    // Create and Add Contact
    CsContact contact = new CsContact();
    contact.setName(serverCapProperties.getContactName());
    contact.setEmail(serverCapProperties.getContactEmail());
    contact.setPhone(serverCapProperties.getContactPhone());
    server.setContact(contact);

    // Add server information
    server.setDescription(serverCapProperties.getDescription());
    server.setName(serverName);
    server.setVendor(vendor);
    server.setSchemaVersion("1.3.1.1");

    // Create functions
    List<CsFunction> function = server.getFunction();
    for (Map.Entry<String, List<DataObject>> pair : functions.entrySet()) {
      CsFunction localFunction = new CsFunction();
      localFunction.setName(pair.getKey());

      // Add objects to function
      List<String> localDataObjects = localFunction.getDataObject();
      List<DataObject> dataObjects = pair.getValue();
      if (dataObjects != null) {
        for (DataObject dataObject : dataObjects) {
          localDataObjects.add(dataObject.getName());
        }
      }

      // Add function to list
      function.add(localFunction);
    }

    // Add server to plural element
    servers.setCapServer(server);

    // Marshal the object to XML
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjCapServers.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    StringWriter writer = new StringWriter();

    // Create object factory and the root element
    com.hashmapinc.tempus.witsml.model.cap.v1311.ObjectFactory
        factory = new com.hashmapinc.tempus.witsml.model.cap.v1311.ObjectFactory();
    JAXBElement<ObjCapServers> finalServers = factory.createCapServers(servers);

    // Create and return the XML string
    jaxbMarshaller.marshal(finalServers, writer);
    return writer.toString();
  }

  /**
   * Creates the 1.4.1.1 XML version of the capabilities object.
   *
   * @return A string representing the 1.4.1.1 XML version of the capabilities object.
   * @throws JAXBException An error occurred in marshalling the object to XML
   */
  private String get1411Object() throws JAXBException {
    // Create root
    com.hashmapinc.tempus.witsml.model.cap.v1411.ObjCapServers servers =
        new com.hashmapinc.tempus.witsml.model.cap.v1411.ObjCapServers();
    servers.setVersion("1.4.1");

    // Create server
    ObjCapServer server =
        new ObjCapServer();
    server.setApiVers("1.4.1");

    // Add Contact
    com.hashmapinc.tempus.witsml.model.cap.v1411.CsContact contact =
        new com.hashmapinc.tempus.witsml.model.cap.v1411.CsContact();
    contact.setName(serverCapProperties.getContactName());
    contact.setEmail(serverCapProperties.getContactEmail());
    contact.setPhone(serverCapProperties.getContactPhone());
    server.setContact(contact);

    // Add Server information
    server.setDescription(serverCapProperties.getDescription());
    server.setName(serverName);
    server.setVendor(vendor);
    server.setSchemaVersion("1.4.1.1");
    server.setChangeDetectionPeriod(serverCapProperties.getChangeDetectionPeriod());

    // Add Timeout Periods
    List<GrowingTimeoutPeriod> timeoutPeriods = server.getGrowingTimeoutPeriod();
    Iterator<Map.Entry<String, Integer>> timeoutIterator = growingTimeouts.entrySet().iterator();
    while (timeoutIterator.hasNext()) {
      Map.Entry<String, Integer> pair = timeoutIterator.next();
      GrowingTimeoutPeriod timeoutPeriod = new GrowingTimeoutPeriod();
      timeoutPeriod.setDataObject(pair.getKey());
      timeoutPeriod.setValue(pair.getValue());
      timeoutPeriods.add(timeoutPeriod);
      timeoutIterator.remove();
    }

    // Add Server functionality
    server.setCascadedDelete(serverCapProperties.getCascadedDelete());
    server.setSupportUomConversion(serverCapProperties.getSupportUomConversion());
    server.setCompressionMethod(serverCapProperties.getCompressionMethod());

    // Add functions
    List<com.hashmapinc.tempus.witsml.model.cap.v1411.CsFunction> function =
        server.getFunction();
    for (Map.Entry<String, List<DataObject>> pair : functions.entrySet()) {
      // Add Function Support
      com.hashmapinc.tempus.witsml.model.cap.v1411.CsFunction localFunction =
          new com.hashmapinc.tempus.witsml.model.cap.v1411.CsFunction();
      localFunction.setName(pair.getKey());

      // Add Object Support
      List<ObjectWithConstraint> localDataObjects = localFunction.getDataObject();
      List<DataObject> dataObjects = pair.getValue();
      if (dataObjects != null) {
        for (DataObject dataObject : dataObjects) {

          // Create object
          ObjectWithConstraint localObject = new ObjectWithConstraint();
          localObject.setValue(dataObject.getName());

          // Set object constraints
          localObject.setMaxDataNodes(dataObject.getMaxDataNodes());
          localObject.setMaxDataPoints(dataObject.getMaxDataPoints());
          localDataObjects.add(localObject);
        }
      }

      // Add function to collection
      function.add(localFunction);
    }

    // Add server to root plural element
    servers.setCapServer(server);

    // Marshal the object to XML
    JAXBContext jaxbContext =
        JAXBContext.newInstance(
            com.hashmapinc.tempus.witsml.model.cap.v1411.ObjCapServers.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    StringWriter writer = new StringWriter();

    // Create object factory and root element
    ObjectFactory factory =
        new ObjectFactory();
    JAXBElement<com.hashmapinc.tempus.witsml.model.cap.v1411.ObjCapServers> finalServers =
        factory.createCapServers(servers);

    // Create and return the XML the string
    jaxbMarshaller.marshal(finalServers, writer);
    return writer.toString();
  }
}
