package io.mocklab.api.controller;

import io.mocklab.api.dto.request.CreateEndpointRequest;
import io.mocklab.api.dto.response.EndpointResponse;
import io.mocklab.api.dto.response.RequestLogResponse;
import io.mocklab.api.service.EndpointService;
import io.mocklab.api.service.RequestLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/endpoints")
@RequiredArgsConstructor
public class EndpointController {

    private final EndpointService endpointService;
    private final RequestLogService requestLogService;

    @PostMapping
    public ResponseEntity<EndpointResponse> createEndpoint(@PathVariable Long workspaceId,
            @Valid @RequestBody CreateEndpointRequest request) {
        EndpointResponse response = endpointService.createEndpoint(workspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EndpointResponse>> getEndpoints(@PathVariable Long workspaceId) {
        List<EndpointResponse> endpoints = endpointService.getEndpointsByWorkspace(workspaceId);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping("/{endpointId}")
    public ResponseEntity<EndpointResponse> getEndpoint(@PathVariable Long workspaceId,
            @PathVariable Long endpointId) {
        EndpointResponse endpoint = endpointService.getEndpointById(endpointId);
        return ResponseEntity.ok(endpoint);
    }

    @PutMapping("/{endpointId}")
    public ResponseEntity<EndpointResponse> updateEndpoint(@PathVariable Long workspaceId,
            @PathVariable Long endpointId,
            @Valid @RequestBody CreateEndpointRequest request) {
        EndpointResponse response = endpointService.updateEndpoint(endpointId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{endpointId}")
    public ResponseEntity<Void> deleteEndpoint(@PathVariable Long workspaceId,
            @PathVariable Long endpointId) {
        endpointService.deleteEndpoint(endpointId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{endpointId}/logs")
    public ResponseEntity<List<RequestLogResponse>> getEndpointLogs(@PathVariable Long workspaceId,
            @PathVariable Long endpointId) {
        List<RequestLogResponse> logs = requestLogService.getLogsByEndpoint(endpointId);
        return ResponseEntity.ok(logs);
    }
}
