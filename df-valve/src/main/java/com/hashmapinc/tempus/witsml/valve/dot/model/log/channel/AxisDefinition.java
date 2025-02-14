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
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ExtensionNameValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "axisStart",
  "axisSpacing",
  "axisCount",
  "axisName",
  "axisPropertyKind",
  "axisUom",
  "uid",
  "order",
  "doubleValues",
  "stringValues",
  "extensionNameValue"
})
public class AxisDefinition {

  @JsonProperty("axisStart")
  private Double axisStart;

  @JsonProperty("axisSpacing")
  private Double axisSpacing;

  @JsonProperty("axisCount")
  private Short axisCount;

  @JsonProperty("axisName")
  private String axisName;

  @JsonProperty("axisPropertyKind")
  private String axisPropertyKind;

  @JsonProperty("axisUom")
  private String axisUom;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("order")
  private Short order;

  @JsonProperty("doubleValues")
  private String doubleValues;

  @JsonProperty("stringValues")
  private String stringValues;

  @JsonProperty("extensionNameValue")
  private List<ExtensionNameValue> extensionNameValue = null;

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

  public static List<AxisDefinition> from1411(
      List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition> axisDefinitions) {
    if (axisDefinitions == null) {
      return Collections.emptyList();
    }
    List<AxisDefinition> axes = new ArrayList<>();

    for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition wmlAxis : axisDefinitions) {

      AxisDefinition axis = new AxisDefinition();
      axis.setAxisCount(wmlAxis.getCount());
      axis.setAxisName(wmlAxis.getName());
      axis.setAxisPropertyKind(wmlAxis.getPropertyType());
      axis.setAxisUom(wmlAxis.getUom());
      axis.setUid(wmlAxis.getUid());
      axis.setOrder(wmlAxis.getOrder());

      if (wmlAxis.getDoubleValues() != null)
        axis.setDoubleValues(String.join(",", wmlAxis.getDoubleValues()));
      if (wmlAxis.getStringValues() != null)
        axis.setStringValues(String.join(",", wmlAxis.getStringValues()));

      axis.setExtensionNameValue(ExtensionNameValue.from1411(wmlAxis.getExtensionNameValue()));

      axes.add(axis);
    }

    return axes;
  }

  public static List<AxisDefinition> from1311(
      List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition> axisDefinitions) {
    if (axisDefinitions == null) {
      return Collections.emptyList();
    }
    List<AxisDefinition> axes = new ArrayList<>();

    for (com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition wmlAxis : axisDefinitions) {

      AxisDefinition axis = new AxisDefinition();
      axis.setAxisCount(wmlAxis.getCount());
      axis.setAxisName(wmlAxis.getName());
      axis.setAxisPropertyKind(wmlAxis.getPropertyType());
      axis.setAxisUom(wmlAxis.getUom());
      axis.setUid(wmlAxis.getUid());
      axis.setOrder(wmlAxis.getOrder());

      List<String> dblValues = new ArrayList<>();
      for (Double val : wmlAxis.getDoubleValues()) {
        dblValues.add(val.toString());
      }

      if (wmlAxis.getDoubleValues() != null) axis.setDoubleValues(String.join(",", dblValues));

      if (wmlAxis.getStringValues() != null)
        axis.setStringValues(String.join(",", wmlAxis.getStringValues()));

      axes.add(axis);
    }

    return axes;
  }

  public static List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition> to1311(
      List<AxisDefinition> axisDefinitions) {
    if (axisDefinitions == null) {
      return Collections.emptyList();
    }

    List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition> wmlAxes = new ArrayList<>();

    for (AxisDefinition dotAxis : axisDefinitions) {
      com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition axis =
          new com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition();

      axis.setCount(dotAxis.getAxisCount());
      axis.setName(dotAxis.getAxisName());
      axis.setPropertyType(dotAxis.getAxisPropertyKind());
      axis.setUom(dotAxis.getAxisUom());
      axis.setUid(dotAxis.getUid());
      axis.setOrder(dotAxis.getOrder());
      if (dotAxis.getDoubleValues() != null) {
        axis.setDoubleValues(
            Stream.of(dotAxis.getDoubleValues().split(","))
                .map(String::valueOf)
                .collect(Collectors.toList()));
      }
      if (dotAxis.getStringValues() != null) {
        axis.setStringValues(
            Stream.of(dotAxis.getStringValues().split(","))
                .map(String::valueOf)
                .collect(Collectors.toList()));
      }

      wmlAxes.add(axis);
    }
    return wmlAxes;
  }

  public static List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition> to1411(
      List<AxisDefinition> axisDefinitions) {
    if (axisDefinitions == null) {
      return Collections.emptyList();
    }

    List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition> wmlAxes = new ArrayList<>();

    for (AxisDefinition dotAxis : axisDefinitions) {
      com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition axis =
          new com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition();

      axis.setCount(dotAxis.getAxisCount());
      axis.setName(dotAxis.getAxisName());
      axis.setPropertyType(dotAxis.getAxisPropertyKind());
      axis.setUom(dotAxis.getAxisUom());
      axis.setUid(dotAxis.getUid());
      axis.setOrder(dotAxis.getOrder());
      if (dotAxis.getDoubleValues() != null) {
        axis.setDoubleValues(
            Stream.of(dotAxis.getDoubleValues().split(","))
                .map(Double::valueOf)
                .collect(Collectors.toList()));
      }
      if (dotAxis.getStringValues() != null) {
        axis.setStringValues(
            Stream.of(dotAxis.getStringValues().split(","))
                .map(String::valueOf)
                .collect(Collectors.toList()));
      }
      // TODO: This has to be addressed in WOL...the set method is not availalble.
      // axis.setExtensionNameValue(ExtensionNameValue.to1411(dotAxis.getExtensionNameValue()));
      wmlAxes.add(axis);
    }
    return wmlAxes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AxisDefinition that = (AxisDefinition) o;
    return Objects.equals(axisStart, that.axisStart)
        && Objects.equals(axisSpacing, that.axisSpacing)
        && Objects.equals(axisCount, that.axisCount)
        && Objects.equals(axisName, that.axisName)
        && Objects.equals(axisUom, that.axisUom)
        && Objects.equals(uid, that.uid)
        && Objects.equals(order, that.order)
        && Objects.equals(doubleValues, that.doubleValues)
        && Objects.equals(stringValues, that.stringValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        axisStart,
        axisSpacing,
        axisCount,
        axisName,
        axisUom,
        uid,
        order,
        doubleValues,
        stringValues);
  }
}
