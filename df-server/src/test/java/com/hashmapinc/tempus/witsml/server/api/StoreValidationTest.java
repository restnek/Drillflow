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

package com.hashmapinc.tempus.witsml.server.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveFactory;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class StoreValidationTest {

  private IValve valve;
  private ValveConfig config;

  private final String minWellQueryTemplate =
      "<wells xmlns=\"http://www.witsml.org/schemas/1series\" "
          + "version=\"1.4.1.1\">"
          + "<well/>"
          + "</wells>";

  @Autowired
  private void setValveConfig(ValveConfig config) {
    this.config = config;
  }

  @BeforeEach
  public void setup() {
    try {
      valve = ValveFactory.buildValve("DoT", config.getConfiguration());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // *****************ADD TO STORE TESTS***************** //

  @Test
  void testSuccess() {
    short resp =
        StoreValidator.validateAddToStore(
            "well",
            "<wells xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n"
                + "<well  uid=\"uid12333\">\n"
                + "\t\t<name>Well Test</name>\n"
                + "</well>\n"
                + "</wells>",
            valve);
    assertThat(resp).isEqualTo((short) 1);
  }

  @Test
  void test408ShouldErrorEmptyMultiLine() {
    short resp = StoreValidator.validateAddToStore("well", "", valve);
    assertThat(resp).isEqualTo((short) -408);
  }

  @Test
  void test486WithSubstring() {
    short resp =
        StoreValidator.validateAddToStore(
            "well",
            "<wellbores xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n"
                + "<wellbore  uid=\"uid12333\">\n"
                + "</wellbore>\n"
                + "</wellbores>",
            valve);
    assertThat(resp).isEqualTo((short) -486);
  }

  @Test
  void test486WithoutSimilarSubstring() {
    short resp =
        StoreValidator.validateAddToStore(
            "well",
            "<logs xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n"
                + "<log  uid=\"uid12333\">\n"
                + "</log>\n"
                + "</logs>",
            valve);
    assertThat(resp).isEqualTo((short) -486);
  }

  @Test
  void test487UnsupportedObjectShouldError() {
      short resp =
        StoreValidator.validateAddToStore(
            "iceCream",
            "<iceCreams xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n"
                + "<iceCream  uid=\"uid12333\">\n"
                + "</iceCream>\n"
                + "</iceCreams>",
            valve);
    assertThat(resp).isEqualTo((short) -487);
  }

  @Test
  void test401DoesNotContainPluralRootElementError() {
      short resp =
        StoreValidator.validateAddToStore(
            "well", "<well uid=\"uid12333\" ><name>test</name></well>", valve);
    assertThat(resp).isEqualTo((short) -401);
  }

  @Test
  void test468DoesNotContainVersionElement() {
      short resp =
        StoreValidator.validateAddToStore(
            "well",
            "<wells xmlns=\"http://www.witsml.org/schemas/131\">\n"
                + "<well  uid=\"uid12333\">\n"
                + "\t\t<name>Well Test</name>\n"
                + "</well>\n"
                + "</wells>",
            valve);
    assertThat(resp).isEqualTo((short) -468);
  }

  @Test
  void test403DoesNotContainADefaultNamespace() {
    short resp =
        StoreValidator.validateAddToStore(
            "well",
            "<wells version=\"1.3.1.1\">\n"
                + "<well  uid=\"uid12333\">\n"
                + "\t\t<name>Well Test</name>\n"
                + "</well>\n"
                + "</wells>",
            valve);
    assertThat(resp).isEqualTo((short) -403);
  }

  // *****************GET CAP TESTS***************** //

  @Test
  void test411InvalidOptionsIn() {
    short resp = StoreValidator.validateGetCap("dataVersion:123");
    assertThat(resp).isEqualTo((short) -411);
  }

  @Test
  void test424CheckForDataVersionExist() {
    short resp = StoreValidator.validateGetCap("hello=123");
    assertThat(resp).isEqualTo((short) -424);
  }

  @Test
  void getCapShouldSuceed() {
    short resp = StoreValidator.validateGetCap("dataVersion=1.3.1.1");
    assertThat(resp).isEqualTo((short) 1);
  }

  // *****************GET FROM STORE TESTS***************** //

  @Test
  void getFromStoreGoodKeyGoodValueShouldSuceed() {
    Map<String, String> testMap = Map.of("requestObjectSelectionCapability", "true");
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) 1);
  }

  @Test
  void getFromStoreNoKeyNoValueShouldSuceed() {
    Map<String, String> testMap = Map.of();
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) 1);
  }

  @Test
  void getFromStoreAnyOtherKeyAnyOtherValueShouldSucceed() {
    Map<String, String> testMap = Map.of("anyOtherKey", "anyOtherValue");
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) 1);
  }

  @Test
  void getFromStoreAnyOtherKeyGoodValueShouldSucceed() {
    // String WMLtypeIn, String xmlIn, Map<String,String> optionsIn, IValve valve;
    Map<String, String> testMap = Map.of("anyOtherKey", "true");
    short resp =
        StoreValidator.validateGetFromStore(
            "well",
            "<wells xmlns=\"http://www.witsml.org/schemas/1series\" version=\"1.4.1.1\"><well/></wells>",
            testMap,
            this.valve);
    assertThat(resp).isEqualTo((short) 1);
  }

  @Test
  void getFromStoreGoodKeyAnyOtherValueExceptWhitespaceOrEmptyShouldSucceed() {
    Map<String, String> testMap = Map.of("requestObjectSelectionCapability", "anyOtherValue");
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) 1);
  }

  @Test
  void getFromStoreGoodKeyEmptyValueShouldFail() {
    Map<String, String> testMap = Map.of("requestObjectSelectionCapability", "");
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) -1001);
  }

  @Test
  void getFromStoreGoodKeyNullValueShouldFail() {
    Map<String, String> testMap = new HashMap<>();
    // Will this get caught by invalid XML (can there be a null key value?)
    testMap.put("requestObjectSelectionCapability", null);
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) -1001);
  }

  @Test
  void getFromStoreNullKeyNullValueShouldSucceed() {
    Map<String, String> testMap = new HashMap<>();
    testMap.put(null, null);
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) 1);
  }

  @Test
  void getFromStoreGoodKeyGoodValueMoreEntriesShouldFail() {
    Map<String, String> testMap = Map.of(
        "requestObjectSelectionCapability", "true",
        "anotherKey", "anotherValue");
    short resp =
        StoreValidator.validateGetFromStore("well", minWellQueryTemplate, testMap, this.valve);
    assertThat(resp).isEqualTo((short) -427);
  }

  @Test
  void getFromStoreGoodKeyGoodValueButXMLInHasUIDShouldFail() {
    Map<String, String> testMap = Map.of("requestObjectSelectionCapability", "true");
    short resp =
        StoreValidator.validateGetFromStore(
            "well",
            "<wells xmlns=\"http://www.witsml.org/schemas/1series\" version=\"1.4.1.1\">"
                + "<well uid=\"uid12333\"></well></wells>",
            testMap,
            this.valve);
    assertThat(resp).isEqualTo((short) -428);
  }
}
