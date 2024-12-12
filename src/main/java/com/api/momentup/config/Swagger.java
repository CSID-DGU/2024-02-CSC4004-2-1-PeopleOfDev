package com.api.momentup.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info);
    }

    Info info = new Info().title("API 명세서").version("0.0.1").description(
            "<h3>API 명세서</h3>");


    public static final ApiResponse SUCCESS_RESPONSE = new ApiResponse()
            .description("성공")
            .content(new Content().addMediaType("application/json",
                    new MediaType().example("{ \"code\": 200, \"message\": \"성공\", \"data\": { \"user\": 1 } }")
            ));

    public static final ApiResponse NOT_FOUND_RESPONSE = new ApiResponse()
            .description("존재하지 않는 리소스 접근")
            .content(new Content().addMediaType("application/json",
                    new MediaType().example("{ \"code\": 404, \"message\": \"User Not found\", \"data\": null }")
            ));
}
