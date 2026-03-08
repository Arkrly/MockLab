package io.mocklab.api.service;

import io.mocklab.api.dto.request.CreateEndpointRequest;
import io.mocklab.api.dto.response.EndpointResponse;
import io.mocklab.api.entity.MockEndpoint;
import io.mocklab.api.entity.Workspace;
import io.mocklab.api.exception.ResourceNotFoundException;
import io.mocklab.api.repository.MockEndpointRepository;
import io.mocklab.api.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EndpointService {

    private final MockEndpointRepository mockEndpointRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public EndpointResponse createEndpoint(Long workspaceId, CreateEndpointRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        // Normalize path: ensure it starts with /
        String path = request.getPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        MockEndpoint endpoint = MockEndpoint.builder()
                .workspace(workspace)
                .method(request.getMethod())
                .path(path)
                .responseBody(request.getResponseBody())
                .statusCode(request.getStatusCode() != null ? request.getStatusCode() : 200)
                .latencyMs(request.getLatencyMs() != null ? request.getLatencyMs() : 0)
                .statefulEnabled(request.getStatefulEnabled() != null ? request.getStatefulEnabled() : false)
                .build();

        endpoint = mockEndpointRepository.save(endpoint);
        log.info("Endpoint created: {} {} in workspace {}", endpoint.getMethod(), endpoint.getPath(), workspaceId);

        return toResponse(endpoint);
    }

    public List<EndpointResponse> getEndpointsByWorkspace(Long workspaceId) {
        return mockEndpointRepository.findByWorkspaceId(workspaceId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EndpointResponse getEndpointById(Long endpointId) {
        MockEndpoint endpoint = mockEndpointRepository.findById(endpointId)
                .orElseThrow(() -> new ResourceNotFoundException("MockEndpoint", "id", endpointId));
        return toResponse(endpoint);
    }

    @Transactional
    public EndpointResponse updateEndpoint(Long endpointId, CreateEndpointRequest request) {
        MockEndpoint endpoint = mockEndpointRepository.findById(endpointId)
                .orElseThrow(() -> new ResourceNotFoundException("MockEndpoint", "id", endpointId));

        String path = request.getPath();
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }

        if (request.getMethod() != null)
            endpoint.setMethod(request.getMethod());
        if (path != null)
            endpoint.setPath(path);
        if (request.getResponseBody() != null)
            endpoint.setResponseBody(request.getResponseBody());
        if (request.getStatusCode() != null)
            endpoint.setStatusCode(request.getStatusCode());
        if (request.getLatencyMs() != null)
            endpoint.setLatencyMs(request.getLatencyMs());
        if (request.getStatefulEnabled() != null)
            endpoint.setStatefulEnabled(request.getStatefulEnabled());

        endpoint = mockEndpointRepository.save(endpoint);
        log.info("Endpoint updated: {} {} (id={})", endpoint.getMethod(), endpoint.getPath(), endpointId);

        return toResponse(endpoint);
    }

    @Transactional
    public void deleteEndpoint(Long endpointId) {
        if (!mockEndpointRepository.existsById(endpointId)) {
            throw new ResourceNotFoundException("MockEndpoint", "id", endpointId);
        }
        mockEndpointRepository.deleteById(endpointId);
        log.info("Endpoint deleted: id={}", endpointId);
    }

    private EndpointResponse toResponse(MockEndpoint endpoint) {
        return EndpointResponse.builder()
                .id(endpoint.getId())
                .workspaceId(endpoint.getWorkspace().getId())
                .method(endpoint.getMethod())
                .path(endpoint.getPath())
                .responseBody(endpoint.getResponseBody())
                .statusCode(endpoint.getStatusCode())
                .latencyMs(endpoint.getLatencyMs())
                .statefulEnabled(endpoint.getStatefulEnabled())
                .createdAt(endpoint.getCreatedAt())
                .build();
    }
}
