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

package com.hashmapinc.tempus.witsml;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * QueryContext is used to hold the state of an individual query to DRILLFLOW and is passed to
 * Valves for query execution
 */
@Getter
@RequiredArgsConstructor
public class QueryContext {

  public final String clientVersion; // the WITSML version used by the client that sent the query
  public final String objectType; // the type of WITSML object being queried for
  public final Map<String, String> optionsIn; // MAP of options_in key/value pairs
  public final String queryXml; // the raw WITSML xml query sent from the client
  public final List<AbstractWitsmlObject> witsmlObjects;
  public final String username;
  public final String password;
  public final String exchangeId;
}
