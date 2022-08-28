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

package com.hashmapinc.tempus.witsml.model.cap.v1311;

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
 * Defines the singular Server Capabilities data type (obj_capServer).
 *
 * <p>Java class for obj_capServer complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="obj_capServer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contact" type="{http://www.witsml.org/api/131}cs_contact" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.witsml.org/api/131}str4096" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.witsml.org/api/131}str64" minOccurs="0"/>
 *         &lt;element name="vendor" type="{http://www.witsml.org/api/131}str64" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.witsml.org/api/131}str64" minOccurs="0"/>
 *         &lt;element name="schemaVersion" type="{http://www.witsml.org/api/131}str64" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.witsml.org/api/131}cs_function" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="apiVers" use="required" type="{http://www.witsml.org/api/131}str16" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "obj_capServer",
    propOrder = {
      "contact",
      "description",
      "name",
      "vendor",
      "version",
      "schemaVersion",
      "function"
    })
public class ObjCapServer {

  protected CsContact contact;
  protected String description;
  protected String name;
  protected String vendor;
  protected String version;
  protected String schemaVersion;

  /*
   * Reference to the live list, not a snapshot. Therefore, any modification you make to the
   * returned list will be present inside the JAXB object. This is why there is not a
   * <CODE>set</CODE> method for the function property.
   *
   * Objects of the following type(s) are allowed in the list {@link CsFunction }
   */
  @Setter(AccessLevel.NONE)
  protected List<CsFunction> function = new ArrayList<>();

  @XmlAttribute(name = "apiVers", required = true)
  protected String apiVers;
}
