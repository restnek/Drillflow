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

package com.hashmapinc.tempus.witsml;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * WitsmlObjectParser is a class that provides simple static methods for parsing witsml objects into
 * AbstractWitsmlObjects
 */
@UtilityClass
public class WitsmlObjectParser {

  /**
   * this method parses log objects
   *
   * @param rawXML - string value with the raw xml to parse
   * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
   * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
   */
  public static List<AbstractWitsmlObject> parseLogObject(String rawXML, String version)
      throws Exception {

    // handle version 1.3.1.1
    if ("1.3.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs.class);
      return new ArrayList<>(objs.getLog());

      // handle version 1.4.1.1
    } else if ("1.4.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class);
      return new ArrayList<>(objs.getLog());
    } else {
      throw new UnsupportedWitsmlVersionException(version);
    }
  }

  /**
   * this method parses trajectory objects
   *
   * @param rawXML - string value with the raw xml to parse
   * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
   * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
   */
  public static List<AbstractWitsmlObject> parseTrajectoryObject(String rawXML, String version)
      throws Exception {

    // handle version 1.3.1.1
    if ("1.3.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys.class);
      return new ArrayList<>(objs.getTrajectory());

      // handle version 1.4.1.1
    } else if ("1.4.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys.class);
      return new ArrayList<>(objs.getTrajectory());

    } else {
      throw new UnsupportedWitsmlVersionException(version);
    }
  }

  /**
   * this method parses well objects
   *
   * @param rawXML - string value with the raw xml to parse
   * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
   * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
   */
  public static List<AbstractWitsmlObject> parseWellObject(String rawXML, String version)
      throws Exception {

    // handle version 1.3.1.1
    if ("1.3.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells.class);
      return new ArrayList<>(objs.getWell());

      // handle version 1.4.1.1
    } else if ("1.4.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells.class);
      return new ArrayList<>(objs.getWell());

    } else {
      throw new UnsupportedWitsmlVersionException(version);
    }
  }

  /**
   * this method parses wellbore objects
   *
   * @param rawXML - string value with the raw xml to parse
   * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
   * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
   */
  public static List<AbstractWitsmlObject> parseWellboreObject(String rawXML, String version)
      throws Exception {

    // handle version 1.3.1.1
    if ("1.3.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores.class);
      return new ArrayList<>(objs.getWellbore());

      // handle version 1.4.1.1
    } else if ("1.4.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores.class);
      return new ArrayList<>(objs.getWellbore());
    } else {
      throw new UnsupportedWitsmlVersionException(version);
    }
  }

  /**
   * this method parses fluids report objects
   *
   * @param rawXML - string value with the raw xml to parse
   * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
   * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
   */
  public static List<AbstractWitsmlObject> parseFluidsReportObject(String rawXML, String version)
      throws Exception {

    // handle version 1.3.1.1
    if ("1.3.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReports objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReports.class);
      return new ArrayList<>(objs.getFluidsReport());

      // handle version 1.4.1.1
    } else if ("1.4.1.1".equals(version)) {
      com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReports objs =
          WitsmlMarshal.deserialize(
              rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReports.class);
      return new ArrayList<>(objs.getFluidsReport());
    } else {
      throw new UnsupportedWitsmlVersionException(version);
    }
  }

  /**
   * this method parses AbstractWitsmlObjects object from the given params and returns them as a
   * list.
   *
   * @param objectType - string, one of “well”, “wellbore”, “log”, “trajectory”, or “fluidsReport”
   * @param rawXML - string, contains the raw xml to parse
   * @param version - string, one of "1.3.1.1" or "1.4.1.1"
   */
  public static List<AbstractWitsmlObject> parse(String objectType, String rawXML, String version)
      throws Exception {

    // parse the object
    switch (objectType) {
      case "log":
        return parseLogObject(rawXML, version);
      case "trajectory":
        return parseTrajectoryObject(rawXML, version);
      case "well":
        return parseWellObject(rawXML, version);
      case "wellbore":
        return parseWellboreObject(rawXML, version);
      case "fluidsReport":
        return parseFluidsReportObject(rawXML, version);
      default:
        throw new WitsmlException("Unsupported witsml object type: " + objectType);
    }
  }
}
