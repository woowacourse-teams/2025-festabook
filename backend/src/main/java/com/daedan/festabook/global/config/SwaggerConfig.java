package com.daedan.festabook.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(createInfo());
    }

    private Info createInfo() {
        return new Info()
                .title("FestaBook API")
                .description("FestaBook API")
                .version("0.0.0");
    }
}
