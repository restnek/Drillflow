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

package com.hashmapinc.tempus.witsml.api;

import com.hashmapinc.tempus.witsml.configuration.properties.ValveProperties;
import com.hashmapinc.tempus.witsml.model.ValveUser;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveFactory;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(basePackages = "com.hashmapinc.tempus.witsml.valve")
public class ValveAuthenticationProvider implements AuthenticationProvider {

  private IValve valve;
  private ValveProperties config;

  @Value("${valve.name}")
  private String valveName;

  @Autowired
  private void setValveConfig(ValveProperties config) {
    this.config = config;
  }

  @PostConstruct
  private void setValve() throws ValveAuthException {
    valve = ValveFactory.buildValve(valveName, config.getConfiguration());
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    UsernamePasswordAuthenticationToken account =
        (UsernamePasswordAuthenticationToken) authentication;
    String password = (String) account.getCredentials();

    try {
      valve.authenticate(name, password);
    } catch (ValveAuthException e) {
      throw new BadCredentialsException(e.getMessage());
    }

    ValveUser user = new ValveUser();
    user.setUserName(name);
    user.setPassword(password);
    user.setToken("testToken");

    UsernamePasswordAuthenticationToken auth;
    auth = new UsernamePasswordAuthenticationToken(user, password, new ArrayList<>());

    return auth;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
