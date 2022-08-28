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

import java.util.ArrayList;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.json.JSONArray;
import org.json.JSONObject;

@UtilityClass
public class JsonUtil {

  public static JSONObject merge(JSONObject req, JSONObject resp) {
    if (!req.has("uid") || JSONObject.NULL.equals(req.get("uid"))) req.put("uid", "");
    // track keys that should be removed before entering recursive merging
    ArrayList<String> keysToRemove = new ArrayList<>();

    // iterate through keys and remove any null keys
    Set<String> keyset = req.keySet();

    // collect keys to remove
    for (String key : keyset) {
      // filter out null keys and empty arrays (equivalent of null) from request
      if (isEmptyArray(req.get(key)) || JSONObject.NULL.equals(req.get(key))) keysToRemove.add(key);
    }

    // remove keys
    for (String keyToRemove : keysToRemove) req.remove(keyToRemove);

    // enter recursion
    return mergeRecursive(req, resp);
  }

  /**
   * This function merges the fields from resp that are missing from req. Only fields that exist in
   * req but have an empty value are merged.
   *
   * @param req - JSONObject to merge into. Existing values here are unchanged
   * @param resp - resp to merge from for blank values in req
   * @return req - fully merged req
   */
  public static JSONObject mergeRecursive(JSONObject req, JSONObject resp) {
    // track keys that should be removed in cleanup
    ArrayList<String> keysToRemove = new ArrayList<>();

    // iterate through keys and merge in place
    Set<String> keyset = req.keySet();
    for (String key : keyset) {
      // check that the response has a value for this key.
      if (!resp.has(key)) {
        if (isEmpty(req.get(key))) keysToRemove.add(key); // remove this key if it's empty
        if (key.equals("customData")) {
          keysToRemove.add(key); // temporary fix for RTC to read 1311 wells
        }
        continue;
      }

      // get values in resp and req as objects for this key
      Object reqObj = req.get(key);
      Object respObj = resp.get(key);

      // do merging below for each possible type
      if (reqObj instanceof JSONObject && respObj instanceof JSONObject) {
        mergeRecursive((JSONObject) reqObj, (JSONObject) respObj); // recursively copy into reqObj
        req.put(key, reqObj); // update req with the updated value for this key

      } else if (reqObj instanceof JSONArray && respObj instanceof JSONArray) {
        if (!isEmpty(respObj)) {
          req.put(key, respObj);
        }
      } else { // handle all basic values (non array, non nested objects)
        req.put(key, respObj);
      }

      // if after all the merging the req value is still empty, add the key to list of removable
      // fields
      if (isEmpty(req.get(key))) keysToRemove.add(key);
    }

    // cleanup fields
    for (String removableKey : keysToRemove) req.remove(removableKey);

    // return the req
    return req;
  }

  /**
   * Checks if either a string, JSONArray, or JSONObject are empty
   *
   * @param obj - object to examine
   * @return boolean - true if emptiness is confirmed, else false
   */
  public static boolean isEmpty(Object obj) {
    // handle nulls
    if (JSONObject.NULL.equals(obj)) return true;

    // handle strings
    if (obj instanceof String) return ((String) obj).isEmpty();

    // handle json array
    if (obj instanceof JSONArray) {
      JSONArray objArray = (JSONArray) obj;
      if (objArray.length() == 0) return true;

      // check if elements are empty too
      boolean elementsAreEmpty = true;
      for (int i = 0; i < objArray.length(); i++) elementsAreEmpty &= isEmpty(objArray.get(i));

      return elementsAreEmpty;
    }

    // handle json objects
    if (obj instanceof JSONObject) {
      // get json obj for easy inspection
      JSONObject jsonObj = (JSONObject) obj;

      // recurse over all children. 1 false results in false for overall check
      boolean jsonObjIsEmpty = true; // true until proven false
      Set<String> keyset = jsonObj.keySet();
      for (String key : keyset) jsonObjIsEmpty = jsonObjIsEmpty && isEmpty(jsonObj.get(key));

      return jsonObjIsEmpty;
    }

    return false;
  }

  /**
   * Checks to see if this JSONObject is an empty array. This is useful for checking if it should be
   * serialized in a JSON output or not.
   *
   * @param obj The Object to examine
   * @return boolean - true if is array AND empty, false if is not array or not empty
   */
  public static boolean isEmptyArray(Object obj) {
    // handle JSONArrays
    if (obj instanceof JSONArray) {
      JSONArray arrObj = (JSONArray) obj;
      return arrObj.length() == 0;
    }
    return false;
  }

  /**
   * Checks for empty Json elements and removes them
   *
   * @param src the source JSON object that needs to have empty elements removed
   * @return The resultant json string with no empty elements
   */
  public static String removeEmptyArrays(JSONObject src) {
    ArrayList<String> keysToRemove = new ArrayList<>();
    for (Object key : src.keySet()) {
      if (JsonUtil.isEmptyArray(src.get(key.toString()))) keysToRemove.add(key.toString());
    }
    for (String key : keysToRemove) {
      src.remove(key);
    }
    return src.toString();
  }

  /**
   * Checks for empty Json elements and removes them
   *
   * @param src the source JSON object that needs to have empty elements removed
   * @return The resultant json string with no empty elements
   */
  public static String removeEmpties(JSONObject src) {
    ArrayList<String> keysToRemove = new ArrayList<>();
    for (Object key : src.keySet()) {
      if (JsonUtil.isEmpty(src.get(key.toString()))) keysToRemove.add(key.toString());
    }
    for (String key : keysToRemove) {
      src.remove(key);
    }
    return src.toString();
  }
}
