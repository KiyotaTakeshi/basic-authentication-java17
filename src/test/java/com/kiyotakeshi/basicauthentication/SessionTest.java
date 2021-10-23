package com.kiyotakeshi.basicauthentication;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// @refer https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-session/spring-session-redis/src/test/java/com/baeldung/spring/session/SessionControllerIntegrationTest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RedisTestConfiguration.class)
public class SessionTest {

    // TODO: Testcontainers 使う
    private static RedisServer redisServer;
    private static TestRestTemplate testRestTemplate;
    private static TestRestTemplate testRestTemplateWithAuth;
    private Jedis jedis;

    public static final Integer REDIS_PORT = 6379;

    private final String EXPECTED = """
                [{"id":1,"name":"mike","department":"sales"},{"id":2,"name":"popcorn","department":"human resources"}]""";

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @LocalServerPort
    private int port;

    private String getTestUrl() {
        return "http://localhost:" + port + "/api/employees";
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        testRestTemplate = new TestRestTemplate();
        testRestTemplateWithAuth = new TestRestTemplate("user", "9c025395-5d88-4610-bef5-bb6949758e0c", null);
        redisServer = new RedisServer(REDIS_PORT);
        redisServer.start();
    }

    @AfterAll
    static void afterAll() {
        redisServer.stop();
    }

    @BeforeEach
    void clearRedisData() {
        jedis = new Jedis("localhost", REDIS_PORT);
        jedis.flushAll();
    }

    @Test
    void testRedisIsEmpty() {
        Set<String> result = jedis.keys("*");
        assertEquals(0, result.size());
    }

    @Test
    void testRequest() {
        ResponseEntity<String> result = testRestTemplateWithAuth.getForEntity(getTestUrl(), String.class);

        assertEquals(EXPECTED, result.getBody());
        Set<String> redisResult = jedis.keys("*");
        assertTrue(redisResult.size() > 0);
    }

    @Test
    void testRequestWithAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        String originalInput = username + ":" + password;
        String encodedValue = Base64.getEncoder().encodeToString(originalInput.getBytes());
        headers.add("Authorization", "Basic " + encodedValue);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> result = testRestTemplate.exchange(getTestUrl(), HttpMethod.GET, httpEntity, String.class);

        assertEquals(EXPECTED, result.getBody());
        Set<String> redisResult = jedis.keys("*");
        assertTrue(redisResult.size() > 0);
    }

    @Test
    void testRequestWithCookieAndRedisFlush() {

        ResponseEntity<String> result = testRestTemplateWithAuth.getForEntity(getTestUrl(), String.class);
        assertEquals(EXPECTED, result.getBody());

        String sessionCookie = result.getHeaders().get("Set-Cookie").get(0).split(";")[0];
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        result = testRestTemplate.exchange(getTestUrl(), HttpMethod.GET, httpEntity, String.class);
        assertEquals(EXPECTED, result.getBody());

        jedis.flushAll();

        result = testRestTemplate.exchange(getTestUrl(), HttpMethod.GET, httpEntity, String.class);
        assertNotEquals(EXPECTED, result.getBody());
    }
}