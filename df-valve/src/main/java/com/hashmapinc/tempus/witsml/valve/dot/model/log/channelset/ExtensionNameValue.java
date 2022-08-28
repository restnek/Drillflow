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
import com.hashmapinc.tempus.WitsmlObjects.v1411.Extensionvalue;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "name",
  "value",
  "measureClass",
  "dTim",
  "index",
  "description",
  "dataType",
  "md",
  "uid"
})
public class ExtensionNameValue {

  @JsonProperty("name")
  private String name;

  @JsonProperty("value")
  private Value value;

  @JsonProperty("measureClass")
  private String measureClass;

  @JsonProperty("dTim")
  private String dTim;

  @JsonProperty("index")
  private Long index;

  @JsonProperty("description")
  private String description;

  @JsonProperty("dataType")
  private String dataType;

  @JsonProperty("md")
  private Md md;

  @JsonProperty("uid")
  private String uid;

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

  public static List<ExtensionNameValue> from1411(
      List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue> extensionNameValues) {

    if (extensionNameValues == null) {
      return Collections.emptyList();
    }

    List<ExtensionNameValue> envs = new ArrayList<>();

    for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue wmlEnv :
        extensionNameValues) {
      ExtensionNameValue env = new ExtensionNameValue();
      env.setName(wmlEnv.getName());
      if (wmlEnv.getValue() != null) {
        com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Value value =
            new com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Value();
        value.setUom(wmlEnv.getValue().getUom());
        value.setValue(wmlEnv.getValue().getValue());
        env.setValue(value);
      }
      env.setMeasureClass(wmlEnv.getMeasureClass());
      env.setDTim(wmlEnv.getDTim().toXMLFormat());
      env.setIndex(wmlEnv.getIndex());
      env.setDescription(wmlEnv.getDescription());
      env.setDataType(wmlEnv.getDataType());

      if (wmlEnv.getMd() != null) {
        Md md = new Md();
        md.setUom(wmlEnv.getMd().getUom());
        md.setValue(wmlEnv.getMd().getValue().toString());
        md.setDatum(wmlEnv.getMd().getDatum());
        env.setMd(md);
      }

      env.setUid(wmlEnv.getUid());
      envs.add(env);
    }
    return envs;
  }

  public static List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue> to1411(
      List<ExtensionNameValue> enValues) throws DatatypeConfigurationException, ParseException {
    if (enValues == null) return Collections.emptyList();

    List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue> wmlEnvs =
        new ArrayList<>();

    for (ExtensionNameValue enValue : enValues) {
      com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue wmlEnv =
          new com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue();

      wmlEnv.setName(enValue.getName());

      if (enValue.getValue() != null) {
        Extensionvalue val = new Extensionvalue();
        val.setUom(enValue.getValue().getUom());
        val.setValue(enValue.getValue().getValue());
        wmlEnv.setValue(val);
      }

      wmlEnv.setMeasureClass(enValue.getMeasureClass());
      wmlEnv.setDTim(convertIsoDateToXML(enValue.getDTim()));
      wmlEnv.setIndex(enValue.getIndex());
      wmlEnv.setDescription(enValue.getDescription());
      wmlEnv.setDataType(enValue.getDataType());
      wmlEnv.setMd(Md.to1411(enValue.md));
      wmlEnv.setUid(enValue.getUid());
      wmlEnvs.add(wmlEnv);
    }

    return wmlEnvs;
  }

  private static XMLGregorianCalendar convertIsoDateToXML(String dateTime)
      throws DatatypeConfigurationException, ParseException {
    DateFormat format = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss.SSSXXX");
    Date date = format.parse(dateTime);

    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(date);

    return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExtensionNameValue that = (ExtensionNameValue) o;
    return Objects.equals(name, that.name)
        && Objects.equals(value, that.value)
        && Objects.equals(measureClass, that.measureClass)
        && Objects.equals(index, that.index)
        && Objects.equals(description, that.description)
        && Objects.equals(dataType, that.dataType)
        && Objects.equals(md, that.md)
        && Objects.equals(uid, that.uid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, measureClass, index, description, dataType, md, uid);
  }
}
