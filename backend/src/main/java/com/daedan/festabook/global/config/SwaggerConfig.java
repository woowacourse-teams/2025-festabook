package com.daedan.festabook.global.config;

import static io.swagger.v3.oas.models.security.SecurityScheme.In;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String ORGANIZATION_SCHEME_NAME = "조직(Organization) ID 값";
    private static final List<String[]> API_GROUPS = List.of(
            new String[]{"일정 API", "/schedules/**"},
            new String[]{"플레이스 API", "/places/**"},
            new String[]{"공지 API", "/announcements/**"}
    );

    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(createInfo())
                .addSecurityItem(createSecurityRequirement())
                .components(createComponents());
    }

    @Bean
    public List<GroupedOpenApi> setupApiGroups() {
        return API_GROUPS.stream()
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

    private static SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList(ORGANIZATION_SCHEME_NAME);
    }

    private static Components createComponents() {
        return new Components()
                .addSecuritySchemes(ORGANIZATION_SCHEME_NAME, new SecurityScheme()
                        .type(Type.APIKEY)
                        .in(In.HEADER)
                        .name("organization"));
    }
}
