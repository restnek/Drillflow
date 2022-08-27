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

package com.hashmapinc.tempus.witsml.server.api.model.cap.v1411;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Java class for cs_function complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="cs_function">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DDataObject" type="{http://www.witsml.org/api/141}objectWithConstraint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.witsml.org/api/141}str64" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "cs_function",
    propOrder = {"dataObject"})
public class CsFunction {

  /*
   * Reference to the live list, not a snapshot. Therefore, any modification you make to the
   * returned list will be present inside the JAXB object. This is why there is not a
   * <CODE>set</CODE> method for the DDataObject property.
   *
   * Objects of the following type(s) are allowed in the list {@link ObjectWithConstraint }
   */
  @Setter(AccessLevel.NONE)
  protected List<ObjectWithConstraint> dataObject = new ArrayList<>();

  @XmlAttribute(name = "name", required = true)
  protected String name;
}
