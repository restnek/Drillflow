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

import com.hashmapinc.tempus.witsml.server.api.model.WmlsGetCapResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WmlsGetFromStoreResponse;
import com.hashmapinc.tempus.witsml.valve.ObjectSelectionConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class StoreImplTests {

  private final String minWellQueryTemplate =
      "<wells xmlns=\"http://www.witsml.org/schemas/1series\" "
          + "version=\"1.4.1.1\">"
          + "<well/>"
          + "</wells>";
  private final String logQueryTemplate =
      "<logs xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n"
          + "<log uidWell=\"Energistics-well-0001\" uidWellbore=\"Energistics-w1-wellbore-0001\" uid=\"HM_fb49c4b1-6638-452d-8043-bba10f109179\">\n"
          + "    <startIndex uom=\"ft\">0</startIndex>\n"
          + "    <endIndex uom=\"ft\">5050</endIndex>\n"
          + "    <logCurveInfo>\n"
          + "      <mnemonic>MTIN</mnemonic>\n"
          + "      <unit></unit>\n"
          + "    </logCurveInfo>\n"
          + "    <logCurveInfo>\n"
          + "      <mnemonic>MTOUT</mnemonic>\n"
          + "      <unit></unit>\n"
          + "    </logCurveInfo>\n"
          + "    <logCurveInfo>\n"
          + "      <mnemonic>MIN_5FT</mnemonic>\n"
          + "      <unit></unit>\n"
          + "    </logCurveInfo>\n"
          + "    <logCurveInfo>\n"
          + "      <mnemonic>MOTOR_RPM</mnemonic>\n"
          + "      <unit></unit>\n"
          + "    </logCurveInfo>\n"
          + "    <logCurveInfo>\n"
          + "      <mnemonic>MRIN</mnemonic>\n"
          + "      <unit></unit>\n"
          + "    </logCurveInfo>\n"
          + "    <logData>\n"
          + "      <data />\n"
          + "    </logData>\n"
          + "  </log>\n"
          + "</logs>";
  @Autowired private StoreImpl witsmlServer;

  @Test
  void contextLoads() {
    assertThat(witsmlServer).isNotNull();
  }

  @Test
  void addToStoreShouldHandleBadInput() {
    assertThat(
            this.witsmlServer
                .addToStore("WMLtypeIn", "XMLin", "OptionsIn", "CapabilitiesIn")
                .getResult())
        .isEqualTo((short) -486);
  }

  @Test
  void getVersionShouldReturnDefaultVersion() {
    assertThat(this.witsmlServer.getVersion().getResult()).contains("1.3.1.1,1.4.1.1");
  }

  //  @Test
  //  void getBaseMsgShouldReturnATextualDescription() {
  //    assertThat(this.witsmlServer.getBaseMsg((short) 412).getResult()).contains("add");
  //  }

  @Test
  void getBaseMsgShouldReturnATextualDescriptionForANegativeNumber() {
    assertThat(this.witsmlServer.getBaseMsg((short) -412).getResult()).contains("add");
  }

  @Test
  void getCapShouldReturnAnXMLForACorrectVersion() {
    WmlsGetCapResponse resp = this.witsmlServer.getCap("dataVersion=1.3.1.1");
    assertThat(resp).isNotNull();
    assertThat(resp.getCapabilitiesOut()).contains("<name>");
    assertThat(resp.getResult()).isEqualTo((short) 1);
  }

  @Test
  void getCapShouldReturn424ForAnIncorrectVersion() {
    WmlsGetCapResponse resp = this.witsmlServer.getCap("dataValue=7");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -424);
    assertThat(resp.getCapabilitiesOut()).isNull();
  }

  @Test
  void getCapShouldReturnTheCorrectErrorForAnEmptyValue() {
    WmlsGetCapResponse resp = this.witsmlServer.getCap("");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -424);
    assertThat(resp.getCapabilitiesOut()).isNull();
  }

  // ***************** GET FROM STORE TESTS ***************** //
  @Test
  void getFrStoreRespWellTrueSucceed() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "well", minWellQueryTemplate, "requestObjectSelectionCapability=true", "");

    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) 1);
    assertThat(resp.getXmlOut()).isEqualTo(ObjectSelectionConstants.WELL_OBJ_SELECTION);
  }

  @Test
  void getFrStoreRespWellboreTrueSucceed() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "wellbore", minWellQueryTemplate, "requestObjectSelectionCapability=true", "");

    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) 1);
    assertThat(resp.getXmlOut()).isEqualTo(ObjectSelectionConstants.WELLBORE_OBJ_SELECTION);
  }

  @Test
  void getFrStoreRespTrajTrueSucceed() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "trajectory", minWellQueryTemplate, "requestObjectSelectionCapability=true", "");

    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) 1);
    assertThat(resp.getXmlOut()).isEqualTo(ObjectSelectionConstants.TRAJECTORY_OBJ_SELECTION);
  }

  @Test
  void getFrStoreRespInvalidTypeFail() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "trapezoid", minWellQueryTemplate, "requestObjectSelectionCapability=true", "");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -487);
  }

  @Test
  void getFrStoreRespTrajValueOtherThanTrueOrNoneFail() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "trajectory", minWellQueryTemplate, "requestObjectSelectionCapability=notTrue", "");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -427);
  }

  @Test
  void getFrStoreRespNoMinQueryTemplateFail() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "trajectory", "", "requestObjectSelectionCapability=true", "");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -408);
  }

  @Test
  void getFrStoreRespNoTypeInFail() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "", minWellQueryTemplate, "requestObjectSelectionCapability=true", "");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -407);
  }

  @Test
  void getFrStoreRespNoVersionFail() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "well",
            // Modified minimum Well query template to have no version
            "<wells xmlns=\"http://www.witsml.org/schemas/1series\" "
                + "version=\"\">"
                + "<well/>"
                + "</wells>",
            "requestObjectSelectionCapability=true",
            "");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -468);
  }

  @Test
  void getFrStoreRespNullKeyFail() {
    // This test will succeed for any valid type; just alternating value of
    // requestObjectSelectionCapability.
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "well", minWellQueryTemplate, "requestObjectSelectionCapability=", "");

    assertThat(resp).isNotNull();
    // Since object selection capability is null, the code returns NULL
    assertThat(resp.getResult()).isEqualTo((short) -411);
  }

  @Test
  void getFrStoreRespWhitespaceKeyFail() {
    // This test will succeed for any valid type; just alternating value of
    // requestObjectSelectionCapability.
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "well", minWellQueryTemplate, "requestObjectSelectionCapability=  ", "");
    assertThat(resp).isNotNull();
    // Since object selection capability is null, the code returns NULL
    assertThat(resp.getResult()).isEqualTo((short) -1001);
  }

  @Test
  void getFrStoreRespXMLHasUIDFail() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "trajectory",
            "<trajectorys"
                + "        xmlns=\"http://www.witsml.org/schemas/1series\""
                + "        version=\"1.4.1.1\">"
                + "    <trajectory uidWell=\"uidWell\" uidWellbore=\"uidWellbore\" uid=\"\">"
                + "        <nameWell/>"
                + "        <nameWellbore/>"
                + "        <name>HM_Plan #2</name>"
                + "        <dTimTrajStart/>"
                + "        <dTimTrajEnd/>"
                + "        <mdMn/>"
                + "        <mdMx/>"
                + "        <serviceCompany/>"
                + "        <magDeclUsed/>"
                + "        <gridCorUsed/>"
                + "        <aziVertSect/>"
                + "        <dispNsVertSectOrig/>"
                + "        <dispEwVertSectOrig/>"
                + "        <definitive/>"
                + "        <memory/>"
                + "        <finalTraj/>"
                + "        <aziRef/>"
                + "        <trajectoryStation uid=\"34ht5\"/>"
                + "        <commonData>"
                + "            <itemState/>"
                + "            <comments/>"
                + "        </commonData>"
                + "    </trajectory>"
                + "</trajectorys>",
            "requestObjectSelectionCapability=true",
            "");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -428);
  }

  @Test
  void getFrStoreRespMoreThanOneOptionsInFail() {
    WmlsGetFromStoreResponse resp =
        this.witsmlServer.getFromStore(
            "well",
            minWellQueryTemplate,
            "requestObjectSelectionCapability=true;dataVersion=1.4.1.1",
            "");
    assertThat(resp).isNotNull();
    assertThat(resp.getResult()).isEqualTo((short) -427);
  }
}
