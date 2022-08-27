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

package com.hashmapinc.tempus.witsml.valve.dot.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

public class DotClient {
  private static final Logger LOG = Logger.getLogger(DotClient.class.getName());
  // private final String URL;
  private final String TOKEN_PATH;
  private final ConcurrentHashMap<String, DecodedJWT> cache;

  /**
   * DotClient constructor
   *
   * @param API_KEY
   * @throws ValveAuthException
   */
  public DotClient(String tokenPath) throws ValveAuthException {
    this.TOKEN_PATH = tokenPath;
    // changed to ConcurrentHashMap to make thread safe
    this.cache = new ConcurrentHashMap<String, DecodedJWT>();
    try {
      buildHttpClient();
    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
      throw new ValveAuthException("Error creating http client: " + e.getMessage(), e);
    }
  }

  /**
   * Generate Token for given creds and save the token in cache
   *
   * @param username
   * @param password
   */
  private void refreshToken(String username, String password, String exchangeId)
      throws ValveAuthException {
    try {
      // build payload for authentication
      String payload = "{\"account\":\"" + username + "\", \"password\":\"" + password + "\"}";
      // build request
      HttpRequestWithBody req = Unirest.post(this.TOKEN_PATH);
      req.header("Content-Type", "application/json").body(payload);

      // send request
      HttpResponse<String> response = new DotRestCommand(req).run();

      // validate response
      int status = response.getStatus();

      LOG.fine(
          "refreshToken call for " + username
              + "returned REST status: " + status); // fine = debug I guess.
      LOG.info(ValveLogging.getLogMsg(exchangeId, "Refreshing Token")); // Stupid JDK 11
      if (201 == status || 200 == status) {
        // get JWT from response
        String tokenString = new JsonNode(response.getBody()).getObject().getString("jwt");
        DecodedJWT decodedJwtToken = JWT.decode(tokenString);
        cache.put(username, decodedJwtToken); // cache response
      } else {
        throw new ValveAuthException(response.getBody());
      }
    } catch (Exception e) {
      throw new ValveAuthException("Error refreshing token: " + e.getMessage(), e);
    }
  }

  /**
   * Refresh the cache if necessary then return the JWT
   *
   * @param username
   * @param password
   * @return JWT from auth endpoint
   * @throws ValveAuthException
   */
  public DecodedJWT getJWT(String username, String password, String exchangeId)
      throws ValveAuthException {
    // refresh token if necessary
    if (!cache.containsKey(username) || isTokenExpired(username))
      refreshToken(username, password, exchangeId);

    return cache.get(username);
  }

  /**
   * Check if JWT Token will expire in the next BUFFER milliseconds
   *
   * @param username
   * @return true if token expires in the next BUFFER milliseconds
   */
  private boolean isTokenExpired(String username) {
    // create buffer
    long JWT_TOKEN_EXPIRY_BUFFER = 5 * 60 * 1000; // 5 minute buffer

    // get time until token expires
    long timeUntilExpiration =
        cache.get(username).getExpiresAt().getTime() - (new Date()).getTime();

    // return true if token expires within the BUFFER time
    return timeUntilExpiration <= JWT_TOKEN_EXPIRY_BUFFER;
  }

  /**
   * removes a user from the cache
   *
   * @param username - key to remove in the cache
   */
  private void removeFromCache(String username) {
      this.cache.remove(username); // TODO: maybe make this thread safe?
  }

  private void buildHttpClient()
      throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
    SSLContext sslcontext =
        SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

    HostnameVerifier verifier =
        new HostnameVerifier() {
          @Override
          public boolean verify(String hostname, SSLSession session) {
            return true;
          }
        };

    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, verifier);
    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
    Unirest.setHttpClient(httpClient);
  }
  /**
   * retry wrapper for private makeRequest
   *
   * @param req - HttpRequest object to execute
   * @param username - auth username
   * @param password - auth password
   */
  public HttpResponse<String> makeRequest(
      HttpRequest req, String username, String password, String exchangeId)
      throws ValveException, UnirestException, ValveAuthException {
    // default numRetries
    int numRetries = 1;
    return this.makeRequest(req, username, password, exchangeId, numRetries);
  }

  /**
   * executes the given unirest request with proper authorization credentials and returns the
   * response string object.
   *
   * <p>on bad credentials, the request is attempted numRetries times
   *
   * @param req - HttpRequest object to execute
   * @param username - auth username
   * @param password - auth password
   * @param numRetries - number of times to retry when auth errors occur
   */
  private HttpResponse<String> makeRequest(
      HttpRequest req, String username, String password, String exchangeId, int numRetries)
      throws UnirestException, ValveAuthException, ValveException {
    // get jwt
    String tokenString = this.getJWT(username, password, exchangeId).getToken();

    LOG.info(ValveLogging.getLogMsg(exchangeId, "Making call to DOT", req));
    // execute request.
    req.header("Authorization", "Bearer " + tokenString); // add auth header
    HttpResponse<String> response = new DotRestCommand(req).run();

    // ensure response is not null
    if (null == response) throw new ValveException("Circuit broken for DoT REST requests");

    LOG.info(ValveLogging.getLogRespMsg(exchangeId, "Received response from DoT", response));

    // check for auth errors.
    int status = response.getStatus();
    if (401 == status) {
      LOG.warning(
          ValveLogging.getLogMsg(
              exchangeId, "Auth token invalid, remove from cache and request new."));
      this.removeFromCache(username); // uncache the jwt for this user

      // if there are retries left, retry.
      if (numRetries > 0) {
        HttpResponse<String> resp =
            this.makeRequest(req, username, password, exchangeId, numRetries - 1);
        LOG.info(ValveLogging.getLogRespMsg(exchangeId, "Received response from DoT", resp));
        return resp;
      } else {
        throw new ValveAuthException("Bad JWT");
      }
    }

    return response;
  }
}
