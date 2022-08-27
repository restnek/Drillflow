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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "title",
  "originator",
  "creation",
  "format",
  "editor",
  "lastUpdate",
  "versionString",
  "description",
  "descriptiveKeywords"
})
public class Citation {

  @JsonProperty("title")
  private String title;

  @JsonProperty("originator")
  private String originator;

  @JsonProperty("creation")
  private String creation;

  @JsonProperty("format")
  private String format;

  @JsonProperty("editor")
  private String editor;

  @JsonProperty("lastUpdate")
  private String lastUpdate;

  @JsonProperty("versionString")
  private String versionString;

  @JsonProperty("description")
  private String description;

  @JsonProperty("descriptiveKeywords")
  private String descriptiveKeywords;

  @JsonIgnore private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty("originator")
  public String getOriginator() {
    return originator;
  }

  @JsonProperty("originator")
  public void setOriginator(String originator) {
    this.originator = originator;
  }

  @JsonProperty("creation")
  public String getCreation() {
    return creation;
  }

  @JsonProperty("creation")
  public void setCreation(String creation) {
    this.creation = creation;
  }

  @JsonProperty("format")
  public String getFormat() {
    return format;
  }

  @JsonProperty("format")
  public void setFormat(String format) {
    this.format = format;
  }

  @JsonProperty("editor")
  public String getEditor() {
    return editor;
  }

  @JsonProperty("editor")
  public void setEditor(String editor) {
    this.editor = editor;
  }

  @JsonProperty("lastUpdate")
  public String getLastUpdate() {
    return lastUpdate;
  }

  @JsonProperty("lastUpdate")
  public void setLastUpdate(String lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  @JsonProperty("versionString")
  public String getVersionString() {
    return versionString;
  }

  @JsonProperty("versionString")
  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("descriptiveKeywords")
  public String getDescriptiveKeywords() {
    return descriptiveKeywords;
  }

  @JsonProperty("descriptiveKeywords")
  public void setDescriptiveKeywords(String descriptiveKeywords) {
    this.descriptiveKeywords = descriptiveKeywords;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Citation citation = (Citation) o;
    return Objects.equals(title, citation.title)
        && Objects.equals(format, citation.format)
        && Objects.equals(editor, citation.editor)
        && Objects.equals(versionString, citation.versionString)
        && Objects.equals(description, citation.description)
        && Objects.equals(descriptiveKeywords, citation.descriptiveKeywords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, format, editor, versionString, description, descriptiveKeywords);
  }
}
