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

package com.hashmapinc.tempus.witsml.valve.dot;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import org.json.JSONObject;
import org.junit.Test;

public class JsonUtilTest {

  @Test
  public void shouldProperlyMerge1311Well() throws Exception {
    String destString = TestUtilities.getResourceAsString("utilTest/well1311dest.json");
    String srcString = TestUtilities.getResourceAsString("utilTest/well1311src.json");

    JSONObject dest = new JSONObject(destString);
    JSONObject src = new JSONObject(srcString);

    JSONObject merged = JsonUtil.merge(dest, src);

    String actual = merged.toString(2);
    String expected = TestUtilities.getResourceAsString("utilTest/well1311merged.json");

    assertEquals(expected, actual);
  }

  @Test
  public void shouldReturnEmptyMergeObject() throws Exception {
    String destString = "{\"fakeField\": null}";
    String srcString = TestUtilities.getResourceAsString("utilTest/well1311src.json");

    JSONObject dest = new JSONObject(destString);
    JSONObject src = new JSONObject(srcString);
    JSONObject merged = JsonUtil.merge(dest, src);

    String actual = merged.toString(2);
    String expected = "{\"uid\": \"uid12333\"}";

    assertEquals(expected, actual);
  }

  @Test
  public void shouldNotBeEmpty() throws IOException {
    String srcString = TestUtilities.getResourceAsString("utilTest/well1311src.json");
    JSONObject src = new JSONObject(srcString);
    boolean isEmpty = JsonUtil.isEmpty(src);
    assertFalse(isEmpty);
  }

  @Test
  public void shouldBeEmpty() {
    String destString = "{\"fakeField\": null}";
    JSONObject dest = new JSONObject(destString);
    boolean isEmpty = JsonUtil.isEmpty(dest);
    assertTrue(isEmpty);
  }
}
