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

import static org.assertj.core.api.Assertions.assertThat;

import com.hashmapinc.tempus.witsml.service.WitsmlUtil;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WitsmlUtilTests {

  @Test
  void parseOptionsIn_shouldParseValidInputString() {
    // create input
    String optionsIn = "coolOption=coolValue;awesomeOption=awesomeValue;chillOption=chillValue";

    // create correct map
    HashMap<String, String> correctMap = new HashMap<>();
    correctMap.put("coolOption", "coolvalue");
    correctMap.put("awesomeOption", "awesomevalue");
    correctMap.put("chillOption", "chillvalue");

    // get parsed map
    Map<String, String> parsedMap = WitsmlUtil.parseOptionsIn(optionsIn);

    // compare the correct and parsed maps - they should be equal
    // compare the incorrect and parsed maps - they should not be equal
    assertThat(parsedMap)
        .isEqualTo(correctMap)
        .isNotEqualTo(Map.of("incorrectOption", "incorrectValue"));
  }
}
