package io.mocklab.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogResponse {

    private Long id;
    private Long workspaceId;
    private Long endpointId;
    private String method;
    private String path;
    private String requestHeaders;
    private String requestBody;
    private Integer responseStatus;
    private Boolean matched;
    private LocalDateTime loggedAt;
}
