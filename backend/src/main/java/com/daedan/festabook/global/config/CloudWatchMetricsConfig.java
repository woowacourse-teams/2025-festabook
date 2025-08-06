package com.daedan.festabook.global.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
@Profile("prod")
public class CloudWatchMetricsConfig {

    private static final String CLOUDWATCH_NAMESPACE = "festabook";
    private static final Duration CLOUDWATCH_STEP = Duration.ofMinutes(1);

    @Bean
    public CloudWatchConfig cloudWatchConfig() {
        return new CloudWatchConfig() {

            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String namespace() {
                return CLOUDWATCH_NAMESPACE;
            }

            @Override
            public Duration step() {
                return CLOUDWATCH_STEP;
            }
        };
    }

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient() {
        return CloudWatchAsyncClient.create();
    }

    @Bean
    public CloudWatchMeterRegistry cloudWatchMeterRegistry(CloudWatchConfig config,
                                                           CloudWatchAsyncClient client) {
        return new CloudWatchMeterRegistry(config, Clock.SYSTEM, client);
    }
}
