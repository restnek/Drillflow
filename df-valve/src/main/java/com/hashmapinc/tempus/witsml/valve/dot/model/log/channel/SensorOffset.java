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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"uom", "value"})
public class SensorOffset {

  @JsonProperty("uom")
  private String uom;

  @JsonProperty("value")
  private String value;

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

  public static SensorOffset from1411(
      com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure sensorOffset) {
    if (sensorOffset == null) return null;

    SensorOffset offset = new SensorOffset();
    offset.setUom(sensorOffset.getUom());
    offset.setValue(sensorOffset.getValue().toString());
    return offset;
  }

  public static SensorOffset from1311(
      com.hashmapinc.tempus.WitsmlObjects.v1311.LengthMeasure sensorOffset) {
    if (sensorOffset == null) return null;

    SensorOffset offset = new SensorOffset();
    offset.setUom(sensorOffset.getUom());
    offset.setValue(sensorOffset.getValue().toString());
    return offset;
  }

  public static com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure to1411(
      SensorOffset sensorOffset) {
    if (sensorOffset == null) return null;

    com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure wmlOffset =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure();
    wmlOffset.setUom(sensorOffset.getUom());
    wmlOffset.setValue(Double.parseDouble(sensorOffset.getValue()));
    return wmlOffset;
  }

  public static com.hashmapinc.tempus.WitsmlObjects.v1311.LengthMeasure to1311(
      SensorOffset sensorOffset) {
    if (sensorOffset == null) return null;

    com.hashmapinc.tempus.WitsmlObjects.v1311.LengthMeasure wmlOffset =
        new com.hashmapinc.tempus.WitsmlObjects.v1311.LengthMeasure();
    wmlOffset.setUom(sensorOffset.getUom());
    wmlOffset.setValue(Double.parseDouble(sensorOffset.getValue()));
    return wmlOffset;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SensorOffset that = (SensorOffset) o;
    return Objects.equals(uom, that.uom) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uom, value);
  }
}
