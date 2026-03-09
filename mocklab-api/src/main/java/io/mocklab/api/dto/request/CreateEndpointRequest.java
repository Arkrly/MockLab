package io.mocklab.api.dto.request;

import io.mocklab.api.enums.HttpMethodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEndpointRequest {

    @NotNull(message = "HTTP method is required")
    private HttpMethodType method;

    @NotBlank(message = "Path is required")
    private String path;

    private String responseBody;

    @Builder.Default
    private Integer statusCode = 200;

    @Builder.Default
    private Integer latencyMs = 0;

    @Builder.Default
    private Boolean statefulEnabled = false;
}
