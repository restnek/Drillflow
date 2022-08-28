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
public class DensData {

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

  public static DensData from1411(
      com.hashmapinc.tempus.WitsmlObjects.v1411.PerLengthMeasure densData) {
    if (densData == null) return null;

    DensData dd = new DensData();
    dd.setUom(densData.getUom());
    dd.setValue(densData.toString());
    return dd;
  }

  public static DensData from1311(
      com.hashmapinc.tempus.WitsmlObjects.v1311.PerLengthMeasure densData) {
    if (densData == null) return null;

    DensData dd = new DensData();
    dd.setUom(densData.getUom());
    dd.setValue(densData.toString());
    return dd;
  }

  public static com.hashmapinc.tempus.WitsmlObjects.v1411.PerLengthMeasure to1411(
      DensData densData) {
    if (densData == null) return null;

    com.hashmapinc.tempus.WitsmlObjects.v1411.PerLengthMeasure wmlDensData =
        new com.hashmapinc.tempus.WitsmlObjects.v1411.PerLengthMeasure();
    wmlDensData.setUom(densData.getUom());
    wmlDensData.setValue(Double.parseDouble(densData.getValue()));
    return wmlDensData;
  }

  public static com.hashmapinc.tempus.WitsmlObjects.v1311.PerLengthMeasure to1311(
      DensData densData) {
    if (densData == null) return null;

    com.hashmapinc.tempus.WitsmlObjects.v1311.PerLengthMeasure wmlDensData =
        new com.hashmapinc.tempus.WitsmlObjects.v1311.PerLengthMeasure();
    wmlDensData.setUom(densData.getUom());
    wmlDensData.setValue(Double.parseDouble(densData.getValue()));
    return wmlDensData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DensData densData = (DensData) o;
    return Objects.equals(uom, densData.uom) && Objects.equals(value, densData.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uom, value);
  }
}
