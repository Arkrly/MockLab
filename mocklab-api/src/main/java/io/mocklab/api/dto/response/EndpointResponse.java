package io.mocklab.api.dto.response;

import io.mocklab.api.enums.HttpMethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointResponse {

    private Long id;
    private Long workspaceId;
    private HttpMethodType method;
    private String path;
    private String responseBody;
    private Integer statusCode;
    private Integer latencyMs;
    private Boolean statefulEnabled;
    private LocalDateTime createdAt;
}
