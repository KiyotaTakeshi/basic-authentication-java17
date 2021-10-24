package com.kiyotakeshi.basicauthentication;

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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RedisTestConfiguration.class)
@Testcontainers
public class SessionTest {

    private static TestRestTemplate testRestTemplate;
    private static TestRestTemplate testRestTemplateWithAuth;

    private Jedis jedis;

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

    // 08:51:44.954 [main] DEBUG 🐳 [redis:6.2.6-alpine] - Starting container: redis:6.2.6-alpine
    // 08:51:44.954 [main] INFO 🐳 [redis:6.2.6-alpine] - Creating container for image: redis:6.2.6-alpine
    // 08:51:44.989 [main] INFO 🐳 [redis:6.2.6-alpine] - Starting container with ID: 110caefe02be8e2b70e54447126d32e550ba9d9306a4c27032edd8bb6bac2382
    // 08:51:45.205 [main] INFO 🐳 [redis:6.2.6-alpine] - Container redis:6.2.6-alpine is starting: 110caefe02be8e2b70e54447126d32e550ba9d9306a4c27032edd8bb6bac2382
    @Container
    private static final GenericContainer REDIS = new GenericContainer(DockerImageName.parse("redis:6.2.6-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        testRestTemplate = new TestRestTemplate();
        testRestTemplateWithAuth = new TestRestTemplate("user", "9c025395-5d88-4610-bef5-bb6949758e0c", null);
    }


    @BeforeEach
    void clearRedisData() {
        String address = REDIS.getHost();
        Integer port = REDIS.getFirstMappedPort();
        jedis = new Jedis(address, port);
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