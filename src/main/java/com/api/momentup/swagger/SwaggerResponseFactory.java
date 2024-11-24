package com.api.momentup.swagger;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class SwaggerResponseFactory {
    public static ApiResponse createSuccessResponse(String description, String example) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().example(example)
                ));
    }

    public static ApiResponse createErrorResponse(String description, String example) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().example(example)
                ));
    }
}
