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
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValveLogging {

  private static final String DELIMITER = " ";

  public static String getLogMsg(String id, String message, AbstractWitsmlObject abstObject) {
    return "ExchangeId: " + id + DELIMITER
        + "Message: " + message + DELIMITER
        + "For Object: " + abstObject.toString();
  }

  public static String getLogMsg(String message) {
    return getLogMsg("n/a", message);
  }

  public static String getLogMsg(String id, String message) {
    return "ExchangeId: " + id + DELIMITER + "Message: " + message;
  }

  public static String getLogMsg(String id, String message, HttpRequest request) {
    try {
      String req = "";
      try {
        if (request != null && request.getBody() != null)
          req = convert(request.getBody().getEntity().getContent(), Charset.defaultCharset());
      } catch (IOException e) {
        req = "Could not parse request";
      }
      return "ExchangeId: " + id + DELIMITER
          + "Message: " + message + DELIMITER
          + "Address: " + request.getUrl() + DELIMITER
          + "Headers: " + request.getHeaders() + DELIMITER
          + "Method: " + request.getHttpMethod().name() + DELIMITER
          + "Payload: " + req;
    } catch (Exception ex) {
      return "done";
    }
  }

  public static String getLogRespMsg(String id, String message, HttpResponse<String> response) {
    String body;
    if (response.getBody() != null) {
      body = response.getBody().replace(System.lineSeparator(), "");
    } else {
      body = "";
    }
    return "ExchangeId: " + id + DELIMITER
        + "Message: " + message + DELIMITER
        + "Headers: " + response.getHeaders() + DELIMITER
        + "Status Code:" + response.getStatus() + DELIMITER
        + "Status Text: " + response.getStatusText() + DELIMITER
        + "Response Body: " + body;
  }

  private static String convert(InputStream inputStream, Charset charset) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
      return br.lines().collect(Collectors.joining(DELIMITER));
    } catch (Exception ex) {
      return "Could not parse request";
    }
  }
}
