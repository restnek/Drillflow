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

package com.hashmapinc.tempus.witsml.valve;

import com.hashmapinc.tempus.witsml.valve.dot.DotValve;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValveFactory {

  public static IValve buildValve(String valveType, Map<String, String> config)
      throws ValveAuthException {

    if ("DoT".equals(valveType)) {
      return new DotValve(config);
    }
    return null;
  }
}
