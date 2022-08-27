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

package com.hashmapinc.tempus.witsml.valve.dot.model.log;

import com.hashmapinc.tempus.WitsmlObjects.Util.log.LogDataHelper;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogData;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class DotLogDataHelper extends LogDataHelper {

  public static String convertDataToWitsml20From1311(
      com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog log) {
    if (log == null) return null;

    return convertStringListToJson(log.getLogData().getData());
  }

  private static String convertDataToWitsml20From1411(
      com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log) {
    if (log == null) return null;

    return convertStringListToJson(log.getLogData().get(0).getData());
  }

  private static String convertStringListToJson(List<String> dataLines) {
    String wml20Data = "[";
    for (int j = 0; j < dataLines.size(); j++) {
      List<String> data = Arrays.asList(dataLines.get(j).split("\\s*,\\s*"));
      for (int i = 0; i < data.size(); i++) {
        if (i == 0) {
          wml20Data = wml20Data + "[[" + data.get(i) + "]";
        } else if (i == 1) {
          wml20Data = wml20Data + ",[" + data.get(i);
        } else {
          wml20Data = wml20Data + ", " + data.get(i);
        }
        if (i == (data.size() - 1)) {
          wml20Data = wml20Data + "]]";
          if (j != (dataLines.size() - 1)) {
            wml20Data = wml20Data + ",";
          }
        }
      }
    }
    wml20Data = wml20Data + "]";
    return wml20Data;
  }

  public static String convertDataToDotFrom1411(
      com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log) {
    String result = "{";
    String wml20MnemonicList = log.getLogData().get(0).getMnemonicList();
    String curveMnemonics = wml20MnemonicList.substring(wml20MnemonicList.indexOf(",") + 1);
    result = result + "\"mnemonicList\":" + "\"" + curveMnemonics + "\"" + ",";
    result = result + "\"data\":" + "\"" + convertDataToWitsml20From1411(log) + "\"}";
    return result;
  }

  public static String convertDataToDotFrom1311(
      com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog log) {
    String result = "{";
    StringBuilder mnemList = new StringBuilder();
    for (int i = 1; i < log.getLogCurveInfo().size(); i++) {
      mnemList.append(log.getLogCurveInfo().get(i).getMnemonic());
      if ((i + 1) < log.getLogCurveInfo().size()) mnemList.append(",");
    }
    result = result + "\"mnemonicList\":" + "\"" + mnemList + "\"" + ",";
    result = result + "\"data\":" + "\"" + convertDataToWitsml20From1311(log) + "\"}";
    return result;
  }

  // Code added to build log data request

  public static String convertChannelDepthDataToDotFrom(
      List<Channel> channels,
      String containerId,
      String sortDesc,
      String startIndex,
      String endIndex) {

    JSONObject dotDataObject = new JSONObject();
    dotDataObject.put("sortDesc", true);
    JSONArray requestedChannels = new JSONArray();
    String indexUnit = "";
    /*        for (Channel wmlCurrentChannel : channels){
        JSONObject dotCurrentChannel = new JSONObject();

        dotCurrentChannel.put("name", wmlCurrentChannel.getMnemonic());
        indexUnit = wmlCurrentChannel.getIndex().get(0).getUom();
        if (wmlCurrentChannel.getStartIndex() != null && !wmlCurrentChannel.getStartIndex().isEmpty()){
            dotCurrentChannel.put("startIndex", wmlCurrentChannel.getStartIndex());
        } else {
            if (startIndex != null)
                dotCurrentChannel.put("startIndex", startIndex);
        }
        if (wmlCurrentChannel.getEndIndex() != null && !wmlCurrentChannel.getEndIndex().isEmpty()){
            dotCurrentChannel.put("endIndex", wmlCurrentChannel.getEndIndex());
        }else {
            if (endIndex != null)
                dotCurrentChannel.put("endIndex", endIndex);
        }
        requestedChannels.put(dotCurrentChannel);
    }*/

    for (Channel wmlCurrentChannel : channels) {
      JSONObject dotCurrentChannel = new JSONObject();

      dotCurrentChannel.put("name", wmlCurrentChannel.getMnemonic());
      indexUnit = wmlCurrentChannel.getIndex().get(0).getUom();
      if (startIndex != null) dotCurrentChannel.put("startIndex", startIndex);

      if (endIndex != null) dotCurrentChannel.put("endIndex", endIndex);

      requestedChannels.put(dotCurrentChannel);
    }

    dotDataObject.put("channels", requestedChannels);
    dotDataObject.put("containerId", containerId);
    dotDataObject.put("indexUnit", indexUnit);
    return dotDataObject.toString();
  }

  public static String convertChannelDepthDataToDotFrom(
      List<Channel> channels, String containerId, String sortDesc) {

    return convertChannelDepthDataToDotFrom(channels, containerId, sortDesc, null, null);
  }

  // code added for logData Transformation

  public static CsLogData convertTo1411FromDot(
      JSONObject object, String indexType, String indexCurve, String indexUnit) {
    JSONArray jsonValues = (JSONArray) object.get("value");
    ArrayList<String> mnems = new ArrayList<String>();
    ArrayList<String> units = new ArrayList<String>();
    mnems.add(indexCurve);
    units.add(indexUnit);
    SortedMap<String, String[]> values = new TreeMap<>();
    Boolean indexCurveExists = false;
    // Check if values contains index Channel
    for (int i = 0; i < jsonValues.length(); i++) {
      JSONObject currentValue = (JSONObject) jsonValues.get(i);
      var mnemonic = currentValue.get("name").toString();
      if (mnemonic.equalsIgnoreCase(indexCurve)) {
        indexCurveExists = true;
        break;
      }
    }

    // Iterate through and get values
    Boolean indexCurvePassed = false;
    for (int i = 0; i < jsonValues.length(); i++) {
      JSONObject currentValue = (JSONObject) jsonValues.get(i);
      var mnemonic = currentValue.get("name").toString();
      var unit = currentValue.get("unit").toString();
      if (mnemonic.equalsIgnoreCase(indexCurve)) {
        indexCurvePassed = true;
        continue;
      }
      mnems.add(mnemonic);
      units.add(unit);
      JSONArray dataPoints = currentValue.getJSONArray("values");
      for (int j = 0; j < dataPoints.length(); j++) {
        JSONObject dataPoint = (JSONObject) dataPoints.get(j);
        String index = dataPoint.keys().next().toString();
        String value = dataPoint.get(index).toString();
        if (!values.containsKey(index)) {
          if (indexCurveExists.booleanValue()) {
            values.put(index, new String[jsonValues.length() - 1]);
          } else {
            values.put(index, new String[jsonValues.length()]);
          }
        }
        if (indexCurvePassed.booleanValue()) {
          values.get(index)[i - 1] = value;
        } else {
          values.get(index)[i] = value;
        }
      }
    }

    // sorting
    CsLogData data = new CsLogData();
    List<String> dataRows = new ArrayList<>();
    if (indexType.equals("depth")) {
      Map<Double, String[]> newMap =
          values.entrySet().stream()
              .collect(
                  Collectors.toMap(
                      entry -> Double.parseDouble(entry.getKey()), Map.Entry::getValue));
      Map<Double, String[]> sortedMap = new TreeMap<Double, String[]>(newMap);

      // Build the Log Data

      data.setMnemonicList(String.join(",", mnems));
      data.setUnitList(String.join(",", units));
      Iterator valueIterator = sortedMap.entrySet().iterator();
      while (valueIterator.hasNext()) {
        Map.Entry pair = (Map.Entry) valueIterator.next();
        StringBuilder logDataRow = new StringBuilder();
        logDataRow.append(pair.getKey());
        logDataRow.append(',');
        logDataRow.append(String.join(",", (String[]) pair.getValue()));
        dataRows.add(logDataRow.toString());
        valueIterator.remove(); // avoids a ConcurrentModificationException
      }

    } else {
      // Build the Log Data
      data.setMnemonicList(String.join(",", mnems));
      data.setUnitList(String.join(",", units));
      Iterator valueIterator = values.entrySet().iterator();
      while (valueIterator.hasNext()) {
        Map.Entry pair = (Map.Entry) valueIterator.next();
        StringBuilder logDataRow = new StringBuilder();
        logDataRow.append(pair.getKey());
        logDataRow.append(',');
        logDataRow.append(String.join(",", (String[]) pair.getValue()));
        dataRows.add(logDataRow.toString());
        valueIterator.remove(); // avoids a ConcurrentModificationException
      }
    }
    data.setData(dataRows);
    return data;
  }

  public static com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogData convertTo1311FromDot(
      JSONObject object, String indexCurve) {
    JSONArray jsonValues = (JSONArray) object.get("value");
    SortedMap<String, String[]> values = new TreeMap<>();
    Boolean indexCurveExists = false;
    for (int i = 0; i < jsonValues.length(); i++) {
      JSONObject currentValue = (JSONObject) jsonValues.get(i);
      var mnemonic = currentValue.get("name").toString();
      if (mnemonic.equalsIgnoreCase(indexCurve)) {
        indexCurveExists = true;
        break;
      }
    }

    Boolean indexCurvePassed = false;
    for (int i = 0; i < jsonValues.length(); i++) {
      JSONObject currentValue = (JSONObject) jsonValues.get(i);
      var mnemonic = currentValue.get("name").toString();
      if (mnemonic.equalsIgnoreCase(indexCurve)) {
        indexCurvePassed = true;
        continue;
      }
      JSONArray dataPoints = currentValue.getJSONArray("values");
      // Iterate through and get values
      for (int j = 0; j < dataPoints.length(); j++) {
        JSONObject dataPoint = (JSONObject) dataPoints.get(j);
        String index = dataPoint.keys().next().toString();
        String value = dataPoint.get(index).toString();
        if (!values.containsKey(index)) {
          if (indexCurveExists.booleanValue()) {
            values.put(index, new String[jsonValues.length() - 1]);
          } else {
            values.put(index, new String[jsonValues.length()]);
          }
        }
        if (indexCurvePassed.booleanValue()) {
          values.get(index)[i - 1] = value;
        } else {
          values.get(index)[i] = value;
        }
      }
    }

    // Build the Log Data
    com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogData data =
        new com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogData();
    List<String> dataRows = new ArrayList<>();
    Iterator valueIterator = values.entrySet().iterator();
    while (valueIterator.hasNext()) {
      Map.Entry pair = (Map.Entry) valueIterator.next();
        String logDataRow = String.valueOf(pair.getKey()) +
                ',' +
                String.join(",", (String[]) pair.getValue());
      dataRows.add(logDataRow);
      valueIterator.remove(); // avoids a ConcurrentModificationException
    }
    data.setData(dataRows);
    return data;
  }
}
