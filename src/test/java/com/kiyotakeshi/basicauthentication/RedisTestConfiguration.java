package com.kiyotakeshi.basicauthentication;

import io.lettuce.core.ClientOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RedisTestConfiguration {
    // In test case,try to connect when @AfterAll test method executed
    // protocol.ConnectionWatchdog     : Cannot reconnect to
    // @see https://github.com/yeongzhiwei/issues-tracker/issues/25
    // this issue says, "For Spring Boot using RedisAutoConfiguration class"
    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return config -> config
                .clientOptions(ClientOptions.builder()
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .build());
    }
}
