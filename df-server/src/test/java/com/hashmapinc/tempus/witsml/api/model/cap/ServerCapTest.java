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

package com.hashmapinc.tempus.witsml.api.model.cap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.hashmapinc.tempus.witsml.service.ServerCap;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

class ServerCapTest {

  @Test
  void createServerCapObject() {
    ServerCap sc = new ServerCap();
    assertNotNull(sc);
  }

  @Test
  void generateDefault1311Object() throws JAXBException {
    ServerCap sc = new ServerCap();
    String obj = sc.getWitsmlObject("1.3.1.1");
    assertThat(obj).contains("1.3.1.1");
  }

  @Test
  void generateDefault1411Object() throws JAXBException {
    ServerCap sc = new ServerCap();
    String obj = sc.getWitsmlObject("1.4.1.1");
    assertThat(obj).contains("1.4.1.1");
  }

  @Test
  void addGrowingTimeout1411() throws JAXBException {
    ServerCap sc = new ServerCap();
    sc.addGrowingTimeoutPeriod("log", 1000);
    String obj = sc.getWitsmlObject("1.4.1.1");
    assertThat(obj)
        .contains("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>");
  }

  @Test
  void removeGrowingTimeout1411() throws JAXBException {
    ServerCap sc = new ServerCap();
    sc.addGrowingTimeoutPeriod("log", 1000);
    String obj = sc.getWitsmlObject("1.4.1.1");
    assertThat(obj)
        .contains("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>");

    sc.removeGrowingTimeoutPeriod("log");
    String obj2 = sc.getWitsmlObject("1.4.1.1");
    assertThat(obj2)
        .doesNotContain("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>");
  }

  @Test
  void addGrowingTimeoutEnsureNotAppear1311() throws JAXBException {
    ServerCap sc = new ServerCap();
    sc.addGrowingTimeoutPeriod("log", 1000);
    String obj = sc.getWitsmlObject("1.3.1.1");
    assertThat(obj)
        .doesNotContain("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>");
  }
}
