package io.mocklab.api.service;

import io.mocklab.api.dto.response.RequestLogResponse;
import io.mocklab.api.entity.RequestLog;
import io.mocklab.api.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestLogService {

    private final RequestLogRepository requestLogRepository;

    public List<RequestLogResponse> getLogsByWorkspace(Long workspaceId) {
        return requestLogRepository.findByWorkspaceIdOrderByLoggedAtDesc(workspaceId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RequestLogResponse> getLogsByWorkspaceAndMatched(Long workspaceId, Boolean matched) {
        return requestLogRepository.findByWorkspaceIdAndMatchedOrderByLoggedAtDesc(workspaceId, matched).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RequestLogResponse> getLogsByEndpoint(Long endpointId) {
        return requestLogRepository.findByEndpointIdOrderByLoggedAtDesc(endpointId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RequestLog save(RequestLog log) {
        return requestLogRepository.save(log);
    }

    private RequestLogResponse toResponse(RequestLog log) {
        return RequestLogResponse.builder()
                .id(log.getId())
                .workspaceId(log.getWorkspace().getId())
                .endpointId(log.getEndpoint() != null ? log.getEndpoint().getId() : null)
                .method(log.getMethod())
                .path(log.getPath())
                .requestHeaders(log.getRequestHeaders())
                .requestBody(log.getRequestBody())
                .responseStatus(log.getResponseStatus())
                .matched(log.getMatched())
                .loggedAt(log.getLoggedAt())
                .build();
    }
}
