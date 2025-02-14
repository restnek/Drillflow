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

package com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hashmapinc.tempus.WitsmlObjects.v1411.TimestampedTimeZone;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "itemState",
  "serviceCategory",
  "comments",
  "acquisitionTimeZone",
  "defaultDatum",
  "privateGroupOnly",
  "extensionAny",
  "extensionNameValue",
  "sourceName",
  "dTimCreation",
  "dTimLastChange"
})
public class CommonData {

  @JsonProperty("itemState")
  private String itemState;

  @JsonProperty("serviceCategory")
  private String serviceCategory;

  @JsonProperty("comments")
  private String comments;

  @JsonProperty("acquisitionTimeZone")
  private List<AcquisitionTimeZone> acquisitionTimeZone = null;

  @JsonProperty("defaultDatum")
  private DefaultDatum defaultDatum;

  @JsonProperty("privateGroupOnly")
  private Boolean privateGroupOnly;

  @JsonProperty("extensionAny")
  private String extensionAny;

  @JsonProperty("extensionNameValue")
  private List<ExtensionNameValue> extensionNameValue = null;

  @JsonProperty("sourceName")
  private String sourceName;

  @JsonProperty("dTimCreation")
  private String dTimCreation;

  @JsonProperty("dTimLastChange")
  private String dTimLastChange;

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

  public static CommonData getCommonDataFrom1311(
      com.hashmapinc.tempus.WitsmlObjects.v1311.CsCommonData witsmlObj) {
    if (witsmlObj == null) {
      return null;
    }
    CommonData cd = new CommonData();
    cd.setItemState(witsmlObj.getItemState());
    cd.setComments(witsmlObj.getComments());
    cd.setSourceName(witsmlObj.getSourceName());
    cd.setDTimCreation(witsmlObj.getDTimLastChange().toXMLFormat());
    cd.setDTimLastChange(witsmlObj.getDTimLastChange().toXMLFormat());
    return cd;
  }

  public static CommonData getCommonDataFrom1411(
      com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData witsmlObj) {
    if (witsmlObj == null) {
      return null;
    }

    CommonData cd = new CommonData();
    cd.setItemState(witsmlObj.getItemState());
    cd.setComments(witsmlObj.getComments());
    cd.setServiceCategory(witsmlObj.getServiceCategory());
    cd.setPrivateGroupOnly(witsmlObj.isPrivateGroupOnly());
    cd.setSourceName(witsmlObj.getSourceName());

    if (witsmlObj.getAcquisitionTimeZone() != null) {
      List<AcquisitionTimeZone> tzs = new ArrayList<>();
      for (TimestampedTimeZone tz : witsmlObj.getAcquisitionTimeZone()) {
        AcquisitionTimeZone atz = new AcquisitionTimeZone();
        atz.setDTim(tz.getDTim().toXMLFormat());
        atz.setValue(tz.getValue());
        tzs.add(atz);
      }
      cd.setAcquisitionTimeZone(tzs);
    }

    if (witsmlObj.getDefaultDatum() != null) {
      DefaultDatum dd = new DefaultDatum();
      dd.setUidRef(witsmlObj.getDefaultDatum().getUidRef());
      dd.setValue(witsmlObj.getDefaultDatum().getValue());
      cd.setDefaultDatum(dd);
    }

    return cd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommonData that = (CommonData) o;
    return Objects.equals(itemState, that.itemState)
        && Objects.equals(serviceCategory, that.serviceCategory)
        && Objects.equals(comments, that.comments)
        && Objects.equals(acquisitionTimeZone, that.acquisitionTimeZone)
        && Objects.equals(defaultDatum, that.defaultDatum)
        && Objects.equals(privateGroupOnly, that.privateGroupOnly)
        && Objects.equals(extensionAny, that.extensionAny)
        && Objects.equals(dTimCreation, that.dTimCreation)
        && Objects.equals(dTimLastChange, that.dTimLastChange);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        itemState,
        serviceCategory,
        comments,
        acquisitionTimeZone,
        defaultDatum,
        privateGroupOnly,
        extensionAny,
        dTimCreation,
        dTimLastChange);
  }
}
