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

package com.hashmapinc.tempus.witsml.valve.dot.graphql;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.TrajectoryConverter;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v20.Trajectory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class GraphQLRespConverter {

  /**
   * Converts a GraphQL response from DoT to a list of singular Abstract witsml objects
   *
   * @param response the GraphQL response from DoT
   * @param objectType the Type of the object received
   * @return An ArrayList of singular objects found
   */
  public static List<AbstractWitsmlObject> convert(String response, String objectType)
      throws IOException {
    JSONObject responseJSON = new JSONObject(response);

    if (!responseJSON.has("data")) {
      return Collections.emptyList();
    }

    JSONObject data = responseJSON.getJSONObject("data");
    switch (objectType) {
      case "well":
        return getWells(data);
      case "wellbore":
        return getWellbores(data);
      case "trajectory":
        return getTrajectories(data);
      default:
        return Collections.emptyList();
    }
  }

  private static List<AbstractWitsmlObject> getWells(JSONObject data) throws IOException {
    ArrayList<AbstractWitsmlObject> foundObjects = new ArrayList<>();
    JSONObject commonData = new JSONObject();
    if (!data.has("wells")) {
      return Collections.emptyList();
    }
    if (data.get("wells") == null) return Collections.emptyList();
    JSONArray wells = data.getJSONArray("wells");

    for (int i = 0; i < wells.length(); i++) {
      // Added code to build commonData and map lastUpdateTimeUtc to dTimLastChange
      JSONObject xformedWell = wells.getJSONObject(i);
      commonData.put("dTimLastChange", xformedWell.get("lastUpdateTimeUtc"));
      xformedWell.put("commonData", commonData);
      ObjWell foundWell = WitsmlMarshal.deserializeFromJSON(xformedWell.toString(), ObjWell.class);
      if (foundWell.getUid() != null) {
        foundObjects.add(foundWell);
      }
    }

    return foundObjects;
  }

  private static List<AbstractWitsmlObject> getWellbores(JSONObject data) throws IOException {
    ArrayList<AbstractWitsmlObject> foundObjects = new ArrayList<>();
    if (!data.has("wellbores")) {
      return Collections.emptyList();
    }
    if (data.get("wellbores") == null) return Collections.emptyList();
    JSONArray wells = data.getJSONArray("wellbores");

    for (int i = 0; i < wells.length(); i++) {
      ObjWellbore foundWell =
          WitsmlMarshal.deserializeFromJSON(wells.get(i).toString(), ObjWellbore.class);
      if (foundWell.getUid() != null) {
        foundObjects.add(foundWell);
      }
    }

    return foundObjects;
  }

  private static List<AbstractWitsmlObject> getTrajectories(JSONObject data) {
    ArrayList<AbstractWitsmlObject> foundObjects = new ArrayList<>();

    if (!data.has("trajectories")) return Collections.emptyList();

    JSONArray trajectories = data.getJSONArray("trajectories");
    if (trajectories == null) return Collections.emptyList();

    for (int i = 0; i < trajectories.length(); i++) {
      try {
        Trajectory trajectory =
            WitsmlMarshal.deserializeFromJSON(trajectories.get(i).toString(), Trajectory.class);
        foundObjects.add(TrajectoryConverter.convertTo1411(trajectory));
      } catch (Exception ignored) {
        // TODO: Remove
      }
    }

    return foundObjects;
  }

  /**
   * This function accepts the JSONObject wellbore graphql response representation and extracts the
   * wellbore's UUID
   */
  public static String getWellboreUuidFromGraphqlResponse(JSONObject response) {
    // check that the response has data
    if (!response.has("data")) return null;

    // check that wellbores exist in the response
    JSONObject data = response.getJSONObject("data");
    if (!data.has("wellbores")) return null;
    if (data.get("wellbores") == null) return null;

    // return the uuid of the first wellbore in the response.
    if (data.getJSONArray("wellbores").length() == 0) return null;

    return data.getJSONArray("wellbores").getJSONObject(0).getString("uuid");
  }

  /**
   * This function accepts the JSONObject wellbore graphql response representation and extracts the
   * wellbore's name
   */
  public static String getWellboreNameFromGraphqlResponse(JSONObject response) {
    // check that the response has data
    if (!response.has("data")) return null;

    // check that wellbores exist in the response
    JSONObject data = response.getJSONObject("data");
    if (!data.has("wellbores")) return null;
    if (data.get("wellbores") == null) return null;

    // return the uuid of the first wellbore in the response.
    return data.getJSONArray("wellbores").getJSONObject(0).getString("name");
  }

  /**
   * This function accepts the JSONObject well graphql response representation and extracts the
   * well's name
   */
  public static String getWellNameFromGraphqlResponse(JSONObject response) {
    // check that the response has data
    if (!response.has("data")) return null;

    // check that wellbores exist in the response
    JSONObject data = response.getJSONObject("data");
    if (!data.has("wells")) return null;
    if (data.get("wells") == null) return null;

    // return the uuid of the first wellbore in the response.
    return data.getJSONArray("wells").getJSONObject(0).getString("name");
  }
}
