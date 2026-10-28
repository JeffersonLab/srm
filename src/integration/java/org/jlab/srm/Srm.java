package org.jlab.srm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * The running app and its services, as started by {@code docker compose -f build.yaml up}.
 *
 * <p>SRM_URL, KEYCLOAK_URL and MAILHOG_URL override the URLs, such as when a port is taken.
 */
final class Srm {

  static final String SRM_URL = env("SRM_URL", "https://localhost:8443/srm");
  static final String KEYCLOAK_URL = env("KEYCLOAK_URL", "http://localhost:8081/auth");
  static final String MAILHOG_URL = env("MAILHOG_URL", "http://localhost:8025");

  /** Has the srm-admin role (container/keycloak) */
  static final String ADMIN = "jdoe";

  /** Has the srm-user role only */
  static final String USER = "user1";

  /** The demo users (container/keycloak) all have this password */
  private static final String PASSWORD = "password";

  private static final String CLIENT_ID = "srm";
  private static final String CLIENT_SECRET = "yHi6W2raPmLvPXoxqMA7VWbLAA2WN0eB";
  private static final String TOKEN_URL =
      KEYCLOAK_URL + "/realms/test-realm/protocol/openid-connect/token";

  private static final Duration READY_TIMEOUT =
      Duration.ofSeconds(Long.parseLong(env("SRM_READY_TIMEOUT_SECONDS", "600")));

  private static final HttpClient HTTP =
      HttpClient.newBuilder()
          .sslContext(sslContext())
          .connectTimeout(Duration.ofSeconds(5))
          .build();

  private static boolean ready = false;

  private Srm() {}

  /** Waits for the app, its database, and Keycloak, as they may still be starting. */
  static synchronized void awaitReady() throws InterruptedException {
    Instant end = Instant.now().plus(READY_TIMEOUT);
    String problem = null;

    while (!ready && Instant.now().isBefore(end)) {
      try {
        // The setup pages need the admin's password login, so Keycloak's setup scripts have run,
        // and the database
        HttpResponse<String> response = getPage("/setup/component-list", token(ADMIN));
        ready = response.statusCode() == 200;
        problem = "GET /setup/component-list returned " + response.statusCode();
      } catch (IOException | RuntimeException | AssertionError e) {
        // Such as a failed token request while Keycloak is still creating the realm
        problem = e.toString();
      }

      if (!ready) {
        Thread.sleep(5_000);
      }
    }

    if (!ready) {
      fail("SRM at " + SRM_URL + " not ready after " + READY_TIMEOUT + ": " + problem);
    }
  }

  /** Gets a token for a demo user with their password. */
  static String token(String username) throws IOException, InterruptedException {
    String basic =
        Base64.getEncoder()
            .encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));

    HttpRequest request =
        HttpRequest.newBuilder(URI.create(TOKEN_URL))
            .header("Authorization", "Basic " + basic)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    encode(
                        List.of(
                            Map.entry("grant_type", "password"),
                            Map.entry("username", username),
                            Map.entry("password", PASSWORD)))))
            .build();

    HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode(), "token request failed: " + response.body());

    return json(response).getString("access_token");
  }

  /** Gets a path of the app; a null token gets it anonymously. */
  static HttpResponse<String> get(String path, String token)
      throws IOException, InterruptedException {
    return send(HttpRequest.newBuilder(URI.create(SRM_URL + path)).GET(), token);
  }

  /**
   * Gets a page of the app, following the app's redirects, such as to a page's default filters, but
   * not those to Keycloak's login.
   */
  static HttpResponse<String> getPage(String path, String token)
      throws IOException, InterruptedException {
    HttpResponse<String> response = get(path, token);

    for (int i = 0; i < 5 && response.statusCode() == 302; i++) {
      String location = response.headers().firstValue("Location").orElseThrow();

      if (!location.startsWith(SRM_URL + "/")) {
        break;
      }

      response = get(location.substring(SRM_URL.length()), token);
    }

    return response;
  }

  /** Posts a form to the app, in order, as parameters such as componentId[] may repeat. */
  static HttpResponse<String> post(String path, String token, List<Map.Entry<String, String>> form)
      throws IOException, InterruptedException {
    return send(
        HttpRequest.newBuilder(URI.create(SRM_URL + path))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(encode(form))),
        token);
  }

  /** Gets the emails MailHog caught that contain the text, newest first. */
  static JsonObject emailsContaining(String text) throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder(
                URI.create(
                    MAILHOG_URL
                        + "/api/v2/search?kind=containing&query="
                        + URLEncoder.encode(text, StandardCharsets.UTF_8)))
            .GET()
            .build();

    HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode(), response.body());

    return json(response);
  }

  static JsonObject json(HttpResponse<String> response) {
    try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
      return reader.readObject();
    }
  }

  private static HttpResponse<String> send(HttpRequest.Builder builder, String token)
      throws IOException, InterruptedException {
    if (token != null) {
      builder.header("Authorization", "Bearer " + token);
    }

    return HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
  }

  private static String encode(List<Map.Entry<String, String>> form) {
    return form.stream()
        .map(
            e ->
                URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                    + "="
                    + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
        .collect(Collectors.joining("&"));
  }

  /**
   * Trusts the usual CAs, plus the self-signed certificate of the jeffersonlab/wildfly image that
   * the app runs in. If that image changes its certificate, save the new one with: {@code openssl
   * s_client -connect localhost:8443 </dev/null | openssl x509 >
   * src/integration/resources/wildfly-localhost.crt}
   */
  private static SSLContext sslContext() {
    Path cacerts = Path.of(System.getProperty("java.home"), "lib", "security", "cacerts");

    try (InputStream defaults = Files.newInputStream(cacerts);
        InputStream wildfly = Srm.class.getResourceAsStream("/wildfly-localhost.crt")) {
      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(defaults, null);
      trustStore.setCertificateEntry(
          "wildfly-localhost",
          CertificateFactory.getInstance("X.509").generateCertificate(wildfly));

      TrustManagerFactory trustManagers =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagers.init(trustStore);

      SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, trustManagers.getTrustManagers(), null);
      return context;
    } catch (IOException | GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }

  private static String env(String name, String defaultValue) {
    String value = System.getenv(name);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
