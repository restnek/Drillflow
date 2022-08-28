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

package com.hashmapinc.tempus.witsml.valve;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IValve {
  /**
   * Retrieve the name of the valve
   *
   * @return The name of the valve
   */
  String getName();

  /**
   * Retrieve the description of the valve
   *
   * @return The description of the valve
   */
  String getDescription();

  /**
   * Gets the object based on the query from the WITSML STORE API
   *
   * @param qc - QueryContext needed to execute the getObject querying
   * @return The resultant object from the query in XML string format
   */
  CompletableFuture<String> getObject(QueryContext qc) throws ValveException;

  /**
   * Gets an XML string with the corresponding type specified
   *
   * @param wmlTypeIn - The WITSML type in from the API query
   * @return An XML string with the corresponding type
   */
  String getObjectSelectionCapability(String wmlTypeIn);

  /**
   * Creates an object
   *
   * @param qc - QueryContext needed to execute the createObject querying
   * @return the UID of the newly created object
   */
  CompletableFuture<String> createObject(QueryContext qc) throws ValveException;

  /**
   * Deletes an object
   *
   * @param qc - QueryContext needed to execute the deleteObject querying
   */
  CompletableFuture<Boolean> deleteObject(QueryContext qc) throws ValveException;

  /**
   * Updates an already existing object
   *
   * @param qc - QueryContext needed to execute the updateObject querying
   * @return status - boolean value; true = success, false = failure
   */
  CompletableFuture<Boolean> updateObject(QueryContext qc) throws ValveException;

  /**
   * Performs authentication
   *
   * @param userName - basic auth username for authentication
   * @param password - basic auth password for authentication
   */
  void authenticate(String userName, String password) throws ValveAuthException;

  /**
   * Return a map of FUNCTION_NAME->ARRAY_OF_SUPPORTED_OBJECTS
   *
   * @return capabilities - map of FUNCTION_NAME->ARRAY_OF_SUPPORTED_OBJECTS
   */
  Map<String, AbstractWitsmlObject[]> getCap();
}
