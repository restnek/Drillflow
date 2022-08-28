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

package com.hashmapinc.tempus.witsml.configuration;

import com.hashmapinc.tempus.witsml.api.StoreImpl;
import java.io.IOException;
import java.net.URL;
import javax.xml.ws.Endpoint;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.transform.XSLTOutInterceptor;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@RequiredArgsConstructor
public class WitsmlApiConfiguration {

  private static final String XSLT_REQUEST_PATH = "interceptor/removeReturn.xsl";

  private final Bus bus;
  private final StoreImpl storeImpl;

  @Value("${wmls.compression}")
  private boolean compression;

  @Bean
  public Endpoint endpoint() throws IOException {
    EndpointImpl endpoint = new EndpointImpl(bus, storeImpl);
    if (compression) endpoint.getOutInterceptors().add(new GZIPOutInterceptor());

    // Removes the <return> element that causes the certification test to fail
    XSLTOutInterceptor returnRemoval =
        new XSLTOutInterceptor(Phase.PRE_STREAM, StaxOutInterceptor.class, null, XSLT_REQUEST_PATH);
    URL wsdl = new ClassPathResource("/schema/WMLS.WSDL").getURL();
    endpoint.getOutInterceptors().add(returnRemoval);
    endpoint.setWsdlLocation(wsdl.toString());
    endpoint.publish("/WMLS");
    LoggingFeature dumbFeature = new LoggingFeature();
    dumbFeature.setPrettyLogging(false);
    endpoint.getFeatures().add(dumbFeature);
    return endpoint;
  }
}
