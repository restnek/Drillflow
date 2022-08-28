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

package com.hashmapinc.tempus.witsml.valve.dot.model.log.channel;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.hashmapinc.tempus.WitsmlObjects.v1311.GenericMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Alias;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelClass;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Citation;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ExtensionNameValue;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Index;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.NominalHoleSize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "uuid",
  "startIndex",
  "endIndex",
  "growingStatus",
  "uid",
  "wellDatum",
  "nullValue",
  "channelState",
  "classIndex",
  "mnemAlias",
  "alternateIndex",
  "sensorOffset",
  "densData",
  "traceOrigin",
  "traceState",
  "namingSystem",
  "mnemonic",
  "dataType",
  "description",
  "uom",
  "source",
  "axisDefinition",
  "timeDepth",
  "channelClass",
  "classWitsml",
  "runNumber",
  "passNumber",
  "loggingCompanyName",
  "loggingCompanyCode",
  "toolName",
  "toolClass",
  "derivation",
  "loggingMethod",
  "nominalHoleSize",
  "pointMetadata",
  "derivedFrom",
  "index",
  "aliases",
  "citation",
  "customData",
  "extensionNameValue",
  "objectVersion",
  "existenceKind"
})
public class Channel {

  @JsonProperty("uuid")
  private String uuid;

  @JsonProperty("startIndex")
  private String startIndex;

  @JsonProperty("endIndex")
  private String endIndex;

  @JsonProperty("growingStatus")
  private String growingStatus;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("wellDatum")
  private WellDatum wellDatum;

  @JsonProperty("nullValue")
  private String nullValue;

  @JsonProperty("channelState")
  private String channelState;

  @JsonProperty("classIndex")
  @JsonDeserialize(using = ClassIndexDeserializer.class)
  private Short classIndex;

  @JsonProperty("mnemAlias")
  private MnemAlias mnemAlias;

  @JsonProperty("alternateIndex")
  private String alternateIndex;

  @JsonProperty("sensorOffset")
  private SensorOffset sensorOffset;

  @JsonProperty("densData")
  private DensData densData;

  @JsonProperty("traceOrigin")
  private String traceOrigin;

  @JsonProperty("traceState")
  private String traceState;

  @JsonProperty("namingSystem")
  private String namingSystem;

  @JsonProperty("mnemonic")
  private String mnemonic;

  @JsonProperty("dataType")
  private String dataType;

  @JsonProperty("description")
  private String description;

  @JsonProperty("uom")
  private String uom;

  @JsonProperty("source")
  private String source;

  @JsonProperty("axisDefinition")
  private List<AxisDefinition> axisDefinition = null;

  @JsonProperty("timeDepth")
  private String timeDepth;

  @JsonProperty("channelClass")
  private ChannelClass channelClass;

  @JsonProperty("classWitsml")
  private String classWitsml;

  @JsonProperty("runNumber")
  private String runNumber;

  @JsonProperty("passNumber")
  private String passNumber;

  @JsonProperty("loggingCompanyName")
  private String loggingCompanyName;

  @JsonProperty("loggingCompanyCode")
  private String loggingCompanyCode;

  @JsonProperty("toolName")
  private String toolName;

  @JsonProperty("toolClass")
  private String toolClass;

  @JsonProperty("derivation")
  private String derivation;

  @JsonProperty("loggingMethod")
  private String loggingMethod;

  @JsonProperty("nominalHoleSize")
  private NominalHoleSize nominalHoleSize;

  @JsonProperty("pointMetadata")
  private List<PointMetadatum> pointMetadata = null;

  @JsonProperty("derivedFrom")
  private List<DerivedFrom> derivedFrom = null;

  @JsonProperty("index")
  private List<Index> index = null;

  @JsonProperty("aliases")
  private List<Alias> aliases = null;

  @JsonProperty("citation")
  private Citation citation;

  @JsonProperty("customData")
  private String customData;

  @JsonProperty("extensionNameValue")
  private List<ExtensionNameValue> extensionNameValue = null;

  @JsonProperty("objectVersion")
  private String objectVersion;

  @JsonProperty("existenceKind")
  private String existenceKind;

  @JsonIgnore
  private final Map<String, Object> additionalProperties = new HashMap<>();

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public static List<Channel> from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlObj) {
    if (witsmlObj.getLogCurveInfo() == null) return Collections.emptyList();

    List<Channel> channels = new ArrayList<>();
    // Create the index once for each channel
    List<Index> indicies = Index.from1411(witsmlObj);

    for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci :
        witsmlObj.getLogCurveInfo()) {
      try {
        Channel channel = new Channel();

        Citation c = new Citation();
        c.setTitle(lci.getMnemonic().getValue());
        c.setDescription(lci.getCurveDescription());
        channel.setClassWitsml(lci.getClassWitsml());
        channel.setCitation(c);
        channel.setUid(lci.getUid());
        channel.setNamingSystem(lci.getMnemonic().getNamingSystem());
        channel.setMnemonic(lci.getMnemonic().getValue());

        if (witsmlObj.getIndexType() != null) {
          if (witsmlObj.getIndexType().toLowerCase().contains("depth")) {
            channel.setTimeDepth("Depth");
          } else {
            if (witsmlObj.getIndexType().toLowerCase().contains("time"))
              channel.setTimeDepth("Time");
          }
        }

        if (lci.getClassIndex() != null) channel.setClassIndex(Short.valueOf(lci.getClassIndex()));

        if (lci.getUnit() == null) {
          channel.setUom("unitless");
        } else {
          channel.setUom(lci.getUnit());
        }

        channel.setIndex(indicies);
        channel.setMnemAlias(MnemAlias.from1411(lci.getMnemAlias()));
        channel.setNullValue(lci.getNullValue());
        channel.setAlternateIndex(lci.isAlternateIndex());
        channel.setDescription(channel.getDescription());
        channel.setSource(lci.getDataSource());
        channel.setTraceState(lci.getTraceState());
        channel.setTraceOrigin(lci.getTraceOrigin());
        channel.setDataType(lci.getTypeLogData());
        channel.setDensData(DensData.from1411(lci.getDensData()));
        channel.setAxisDefinition(AxisDefinition.from1411(lci.getAxisDefinition()));
        channel.setExtensionNameValue(ExtensionNameValue.from1411(lci.getExtensionNameValue()));
        channel.setWellDatum(WellDatum.from1411(lci.getWellDatum()));
        channel.setSensorOffset(SensorOffset.from1411(lci.getSensorOffset()));
        channels.add(channel);
      } catch (Exception ignored) {
      }
    }
    return channels;
  }

  public static List<Channel> from1311(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog witsmlObj) {
    if (witsmlObj.getLogCurveInfo() == null) return Collections.emptyList();

    List<Channel> channels = new ArrayList<>();
    // Create the index once for each channel
    List<Index> indicies = Index.from1311(witsmlObj);

    for (com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo lci :
        witsmlObj.getLogCurveInfo()) {
      try {
        Channel channel = new Channel();

        Citation c = new Citation();
        c.setTitle(lci.getMnemonic());

        channel.setCitation(c);
        channel.setUid(lci.getUid());
        channel.setMnemonic(lci.getMnemonic());

        if (witsmlObj.getIndexType().toLowerCase().contains("depth")) channel.setTimeDepth("Depth");
        else channel.setTimeDepth("Time");
        channel.setClassWitsml(lci.getClassWitsml());

        if (lci.getUnit() == null) {
          channel.setUom("unitless");
        } else {
          channel.setUom(lci.getUnit());
        }

        channel.setIndex(indicies);
        channel.setNullValue(lci.getNullValue());
        channel.setAlternateIndex(lci.isAlternateIndex());
        channel.setDescription(channel.getDescription());
        channel.setSource(lci.getDataSource());
        channel.setTraceState(lci.getTraceState());
        channel.setTraceOrigin(lci.getTraceOrigin());
        channel.setDataType(lci.getTypeLogData());
        channel.setWellDatum(WellDatum.from1311(lci.getWellDatum()));
        channel.setSensorOffset(SensorOffset.from1311(lci.getSensorOffset()));
        channel.setDensData(DensData.from1311(lci.getDensData()));
        channel.setMnemAlias(MnemAlias.from1311(lci));
        channel.setAxisDefinition(AxisDefinition.from1311(lci.getAxisDefinition()));

        channels.add(channel);
      } catch (Exception ignored) {
      }
    }
    return channels;
  }

  public static List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> to1411(
      List<Channel> channels, ChannelSet channelSet) {
    List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> curves = new ArrayList<>();

    if (channels == null || channels.isEmpty()) {
      return Collections.emptyList();
    }

    var indexLci = new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();
    var indexName = new ShortNameStruct();
    var channelSetIndex = channelSet.getIndex().get(0);
    indexName.setValue(channelSetIndex.getMnemonic());
    indexLci.setMnemonic(indexName);
    indexLci.setUnit(channelSetIndex.getUom());
    indexLci.setUid("lci-" + Math.abs(channelSetIndex.getMnemonic().hashCode()));
    if (channelSet.getTimeDepth().toLowerCase().contains("time")) {
      indexLci.setTypeLogData("date time");
      indexLci.setMinDateTimeIndex(channelSet.getStartIndex());
      indexLci.setMaxDateTimeIndex(channelSet.getEndIndex());
    } else {
      indexLci.setTypeLogData("double");
      com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure =
          new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
      minMeasure.setUom("m");
      if (channelSet.getStartIndex() != null) {
        minMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
      }
      indexLci.setMinIndex(minMeasure);
      com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure =
          new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
      maxMeasure.setUom("m");
      if (channelSet.getEndIndex() != null) {
        maxMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
      }
      indexLci.setMaxIndex(maxMeasure);
    }
    var hasIndexChannel = false;

    for (Channel c : channels) {
      try {
        if (c.getMnemonic().equals(channelSetIndex.getMnemonic())) {
          hasIndexChannel = true;
        }
        com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci =
            new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();
        ShortNameStruct name = new ShortNameStruct();
        name.setValue(c.getMnemonic());
        name.setNamingSystem(c.getNamingSystem());
        lci.setMnemonic(name);
        lci.setAlternateIndex(c.getAlternateIndex());
        lci.setClassWitsml(c.getClassWitsml());
        // NOTE: WE WILL ALWAYS SET THE INDEX TO THE FIRST COLUMN
        lci.setCurveDescription(c.getCitation().getDescription());
        lci.setDataSource(c.getSource());
        lci.setTraceOrigin(c.getTraceOrigin());
        lci.setTraceState(c.getTraceState());
        lci.setTypeLogData(c.getDataType());
        lci.setUid(c.getUid());
        lci.setUnit(c.getUom());
        lci.setNullValue(c.getNullValue());
        lci.setMnemAlias(MnemAlias.to1411(c.mnemAlias));
        lci.setAxisDefinition(AxisDefinition.to1411(c.getAxisDefinition()));
        lci.setDensData(DensData.to1411(c.getDensData()));
        lci.setSensorOffset(SensorOffset.to1411(c.getSensorOffset()));
        lci.setWellDatum(WellDatum.to1411(c.getWellDatum()));
        lci.setClassIndex(String.valueOf(c.getClassIndex()));
        if (c.getTimeDepth().toLowerCase().contains("time")) {
          if (c.getStartIndex() != null) {
            lci.setMinDateTimeIndex(c.getStartIndex());
          } else {
            lci.setMinDateTimeIndex(channelSet.getStartIndex());
          }

          if (c.getEndIndex() != null) {
            lci.setMaxDateTimeIndex(c.getEndIndex());
          } else {
            lci.setMaxDateTimeIndex(channelSet.getEndIndex());
          }
        } else {
          if (c.getStartIndex() != null) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
            minMeasure.setUom("m");
            minMeasure.setValue(Double.parseDouble(c.getStartIndex()));
            lci.setMinIndex(minMeasure);
          } else {
            com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
            minMeasure.setUom("m");
            minMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
            lci.setMinIndex(minMeasure);
          }
          if (c.getEndIndex() != null) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
            maxMeasure.setUom("m");
            maxMeasure.setValue(Double.parseDouble(c.getEndIndex()));
            lci.setMaxIndex(maxMeasure);
          } else {
            com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
            maxMeasure.setUom("m");
            maxMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
            lci.setMaxIndex(maxMeasure);
          }
        }
        // Need to address this in wol...does not exist
        // lci.getExtensionNameValue()
        curves.add(lci);
      } catch (Exception ignored) {
      }
    }
    if (!hasIndexChannel) {
      curves.add(0, indexLci);
    }
    return curves;
  }

  public static List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> to1411WithLogData(
      List<Channel> channels, JSONObject object, ChannelSet channelSet) {
    JSONArray jsonValues = (JSONArray) object.get("value");

    List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> curves = new ArrayList<>();
    if (channels == null || channels.isEmpty()) return Collections.emptyList();

    var indexLci = new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();
    var indexName = new ShortNameStruct();
    var channelSetIndex = channelSet.getIndex().get(0);
    indexName.setValue(channelSetIndex.getMnemonic());
    indexLci.setMnemonic(indexName);
    indexLci.setUnit(channelSetIndex.getUom());
    indexLci.setUid("lci-" + Math.abs(channelSetIndex.getMnemonic().hashCode()));
    if (channelSet.getTimeDepth().toLowerCase().contains("time")) {
      indexLci.setTypeLogData("date time");
      indexLci.setMinDateTimeIndex(channelSet.getStartIndex());
      indexLci.setMaxDateTimeIndex(channelSet.getEndIndex());
    } else {
      indexLci.setTypeLogData("double");
      com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure =
          new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
      minMeasure.setUom("m");
      if (channelSet.getStartIndex() != null) {
        minMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
      }
      indexLci.setMinIndex(minMeasure);
      com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure =
          new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
      maxMeasure.setUom("m");
      if (channelSet.getEndIndex() != null) {
        maxMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
      }
      indexLci.setMaxIndex(maxMeasure);
    }
    var hasIndexChannel = false;
    for (Channel c : channels) {
      try {
        if (c.getMnemonic().equals(channelSetIndex.getMnemonic())) {
          hasIndexChannel = true;
        }
        com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci =
            new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();
        ShortNameStruct name = new ShortNameStruct();
        name.setValue(c.getMnemonic());
        name.setNamingSystem(c.getNamingSystem());
        lci.setMnemonic(name);
        lci.setAlternateIndex(c.getAlternateIndex());
        lci.setClassWitsml(c.getClassWitsml());
        // NOTE: WE WILL ALWAYS SET THE INDEX TO THE FIRST COLUMN
        lci.setCurveDescription(c.getCitation().getDescription());
        lci.setDataSource(c.getSource());
        lci.setTraceOrigin(c.getTraceOrigin());
        lci.setTraceState(c.getTraceState());
        lci.setTypeLogData(c.getDataType());
        lci.setUid(c.getUid());
        lci.setUnit(c.getUom());
        lci.setNullValue(c.getNullValue());
        lci.setMnemAlias(MnemAlias.to1411(c.mnemAlias));
        lci.setAxisDefinition(AxisDefinition.to1411(c.getAxisDefinition()));
        lci.setDensData(DensData.to1411(c.getDensData()));
        lci.setSensorOffset(SensorOffset.to1411(c.getSensorOffset()));
        lci.setWellDatum(WellDatum.to1411(c.getWellDatum()));
        lci.setClassIndex(String.valueOf(c.getClassIndex()));
        if (c.getTimeDepth().toLowerCase().contains("time")) {
          // logic to implement startIndex and endIndex from logData
          for (int i = 0; i < jsonValues.length(); i++) {
            JSONObject currentValue = (JSONObject) jsonValues.get(i);
            if (currentValue.get("name").toString().equalsIgnoreCase(c.getMnemonic())) {
              JSONArray dataPoints = currentValue.getJSONArray("values");
              if (dataPoints.length() > 0) {
                for (int j = 0; j < dataPoints.length(); j++) {
                  JSONObject dataPoint = (JSONObject) dataPoints.get(j);
                  String maxIndex = dataPoint.keys().next().toString();
                  String maxValue = dataPoint.get(maxIndex).toString();
                  if (!maxValue.equalsIgnoreCase("null") && !maxValue.equalsIgnoreCase("")) {
                    lci.setMaxDateTimeIndex(maxIndex);
                    j = dataPoints.length();
                    JSONArray toReturn = new JSONArray();
                    int length = dataPoints.length() - 1;
                    for (int k = length; k >= 0; k--) {
                      toReturn.put(dataPoints.getJSONObject(k));
                    }
                    for (int l = 0; l < toReturn.length(); l++) {
                      JSONObject minDataPoint = (JSONObject) toReturn.get(l);
                      String minIndex = minDataPoint.keys().next().toString();
                      String minValue = minDataPoint.get(minIndex).toString();
                      if (!minValue.equalsIgnoreCase("null") && !minValue.equalsIgnoreCase("")) {
                        lci.setMinDateTimeIndex(minIndex);
                        l = toReturn.length();
                      }
                    }
                  }
                }
              } else {
                lci.setMaxDateTimeIndex(channelSet.getEndIndex());
                lci.setMinDateTimeIndex(channelSet.getStartIndex());
              }
            }
          }
        } else {
          // logic to implement startIndex and endIndex from logData
          for (int i = 0; i < jsonValues.length(); i++) {
            JSONObject currentValue = (JSONObject) jsonValues.get(i);
            if (currentValue.get("name").toString().equalsIgnoreCase(c.getMnemonic())) {
              JSONArray dataPoints = currentValue.getJSONArray("values");
              if (dataPoints.length() > 0) {
                for (int j = 0; j < dataPoints.length(); j++) {
                  JSONObject dataPoint = (JSONObject) dataPoints.get(j);
                  String maxIndex = dataPoint.keys().next().toString();
                  String maxValue = dataPoint.get(maxIndex).toString();
                  if (!maxValue.equalsIgnoreCase("null") && !maxValue.equalsIgnoreCase("")) {
                    com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure =
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                    maxMeasure.setUom("m");
                    maxMeasure.setValue(Double.parseDouble(maxIndex));
                    lci.setMaxIndex(maxMeasure);
                    j = dataPoints.length();

                    JSONArray toReturn = new JSONArray();
                    int length = dataPoints.length() - 1;
                    for (int k = length; k >= 0; k--) {
                      toReturn.put(dataPoints.getJSONObject(k));
                    }
                    for (int l = 0; l < toReturn.length(); l++) {
                      JSONObject minDataPoint = (JSONObject) toReturn.get(l);
                      String minIndex = minDataPoint.keys().next().toString();
                      String minValue = minDataPoint.get(minIndex).toString();
                      if (!minValue.equalsIgnoreCase("null") && !minValue.equalsIgnoreCase("")) {
                        com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure =
                            new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                        minMeasure.setUom("m");
                        minMeasure.setValue(Double.parseDouble(minIndex));
                        lci.setMinIndex(minMeasure);
                        l = toReturn.length();
                      }
                    }
                  }
                }
              } else {
                com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure =
                    new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                maxMeasure.setUom("m");
                maxMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
                lci.setMaxIndex(maxMeasure);

                com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure =
                    new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                minMeasure.setUom("m");
                minMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
                lci.setMinIndex(minMeasure);
              }
            }
          }
        }
        curves.add(lci);

      } catch (Exception ignored) {
      }
    }
    if (!hasIndexChannel) {
      curves.add(0, indexLci);
    }
    return curves;
  }

  public static List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> to1311(
      List<Channel> channels) {
    List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> curves = new ArrayList<>();

    if (channels == null || channels.isEmpty()) return Collections.emptyList();

    for (Channel c : channels) {
      try {
        com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo lci =
            new com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo();
        lci.setMnemonic(c.getCitation().getTitle());
        lci.setAlternateIndex(c.getAlternateIndex());
        lci.setClassWitsml(c.getClassWitsml());
        // NOTE: WE WILL ALWAYS SET THE INDEX TO THE FIRST COLUMN
        lci.setColumnIndex("1");
        lci.setCurveDescription(c.getCitation().getDescription());
        lci.setDataSource(c.getSource());
        lci.setTraceOrigin(c.getTraceOrigin());
        lci.setTraceState(c.getTraceState());
        lci.setTypeLogData(c.getDataType());
        lci.setUid(c.getUid());
        lci.setUnit(c.getUom());
        lci.setNullValue(c.getNullValue());
        lci.setMnemAlias(c.getMnemAlias().getValue());
        lci.setAxisDefinition(AxisDefinition.to1311(c.getAxisDefinition()));
        lci.setDensData(DensData.to1311(c.getDensData()));
        lci.setSensorOffset(SensorOffset.to1311(c.getSensorOffset()));
        lci.setWellDatum(WellDatum.to1311(c.getWellDatum()));
        lci.setColumnIndex(String.valueOf(c.classIndex));

        if (c.getTimeDepth().toLowerCase().contains("time")) {
          lci.setMinDateTimeIndex(c.getStartIndex());
          lci.setMaxDateTimeIndex(c.getEndIndex());
        } else {
          GenericMeasure minMeasure = new GenericMeasure();
          minMeasure.setUom("m");
          minMeasure.setValue(Double.parseDouble(c.getStartIndex()));
          lci.setMinIndex(minMeasure);
          GenericMeasure maxMeasure = new GenericMeasure();
          maxMeasure.setUom("m");
          maxMeasure.setValue(Double.parseDouble(c.getEndIndex()));
          lci.setMaxIndex(maxMeasure);
        }

        curves.add(lci);
      } catch (Exception ignored) {
      }
    }
    return curves;
  }

  public static String channelListToJson(List<Channel> channels) throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    om.setDateFormat(new StdDateFormat());
    return om.writerWithDefaultPrettyPrinter().writeValueAsString(channels);
  }

  public static List<Channel> jsonToChannelList(String channelsList) throws ValveException {
    return fromJSON(new TypeReference<List<Channel>>() {}, channelsList);
  }

  public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket)
      throws ValveException {
    T data = null;

    try {
      data = new ObjectMapper().readValue(jsonPacket, type);
    } catch (Exception e) { // Handle the problem
      throw new ValveException(e.getMessage());
    }
    return data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Channel channel = (Channel) o;
    return Objects.equals(uuid, channel.uuid)
        && Objects.equals(uid, channel.uid)
        && Objects.equals(wellDatum, channel.wellDatum)
        && Objects.equals(nullValue, channel.nullValue)
        && Objects.equals(channelState, channel.channelState)
        && Objects.equals(classIndex, channel.classIndex)
        && Objects.equals(mnemAlias, channel.mnemAlias)
        && Objects.equals(alternateIndex, channel.alternateIndex)
        && Objects.equals(sensorOffset, channel.sensorOffset)
        && Objects.equals(densData, channel.densData)
        && Objects.equals(traceOrigin, channel.traceOrigin)
        && Objects.equals(traceState, channel.traceState)
        && Objects.equals(namingSystem, channel.namingSystem)
        && Objects.equals(mnemonic, channel.mnemonic)
        && Objects.equals(dataType, channel.dataType)
        && Objects.equals(description, channel.description)
        && Objects.equals(uom, channel.uom)
        && Objects.equals(source, channel.source)
        && Objects.equals(axisDefinition, channel.axisDefinition)
        && Objects.equals(timeDepth, channel.timeDepth)
        && Objects.equals(channelClass, channel.channelClass)
        && Objects.equals(classWitsml, channel.classWitsml)
        && Objects.equals(runNumber, channel.runNumber)
        && Objects.equals(passNumber, channel.passNumber)
        && Objects.equals(loggingCompanyName, channel.loggingCompanyName)
        && Objects.equals(loggingCompanyCode, channel.loggingCompanyCode)
        && Objects.equals(toolName, channel.toolName)
        && Objects.equals(toolClass, channel.toolClass)
        && Objects.equals(derivation, channel.derivation)
        && Objects.equals(loggingMethod, channel.loggingMethod)
        && Objects.equals(nominalHoleSize, channel.nominalHoleSize)
        && Objects.equals(index, channel.index)
        && Objects.equals(aliases, channel.aliases)
        && Objects.equals(citation, channel.citation)
        && Objects.equals(customData, channel.customData)
        && Objects.equals(objectVersion, channel.objectVersion)
        && Objects.equals(existenceKind, channel.existenceKind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        uuid,
        uid,
        wellDatum,
        nullValue,
        channelState,
        classIndex,
        mnemAlias,
        alternateIndex,
        sensorOffset,
        densData,
        traceOrigin,
        traceState,
        namingSystem,
        mnemonic,
        dataType,
        description,
        uom,
        source,
        axisDefinition,
        timeDepth,
        channelClass,
        classWitsml,
        runNumber,
        passNumber,
        loggingCompanyName,
        loggingCompanyCode,
        toolName,
        toolClass,
        derivation,
        loggingMethod,
        nominalHoleSize,
        index,
        aliases,
        citation,
        customData,
        objectVersion,
        existenceKind);
  }
}
