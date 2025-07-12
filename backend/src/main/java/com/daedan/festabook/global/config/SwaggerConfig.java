package com.daedan.festabook.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final List<String[]> API_GROUP = List.of(
            new String[]{"일정 API", "/schedules/**"},
            new String[]{"플레이스 API", "/places/**"},
            new String[]{"공지 API", "/announcements/**"}
    );

    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(createInfo());
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        return API_GROUP.stream()
                .map(group -> GroupedOpenApi.builder()
                        .group(group[0])
                        .pathsToMatch(group[1])
                        .build())
                .toList();
    }

    private Info createInfo() {
        return new Info()
                .title("FestaBook API")
                .description("FestaBook API")
                .version(buildProperties.getVersion());
    }
}
