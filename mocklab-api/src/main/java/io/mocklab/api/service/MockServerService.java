package io.mocklab.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.mocklab.api.entity.MockEndpoint;
import io.mocklab.api.entity.RequestLog;
import io.mocklab.api.entity.StatefulRecord;
import io.mocklab.api.entity.Workspace;
import io.mocklab.api.enums.HttpMethodType;
import io.mocklab.api.repository.MockEndpointRepository;
import io.mocklab.api.repository.RequestLogRepository;
import io.mocklab.api.repository.StatefulRecordRepository;
import io.mocklab.api.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockServerService {

    private final MockEndpointRepository mockEndpointRepository;
    private final StatefulRecordRepository statefulRecordRepository;
    private final RequestLogRepository requestLogRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Transactional
    public ResponseEntity<String> handleMockRequest(Long workspaceId,
            String requestPath,
            HttpMethodType method,
            String requestBody,
            HttpServletRequest servletRequest) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Workspace not found\"}");
        }

        // Normalize the request path
        if (!requestPath.startsWith("/")) {
            requestPath = "/" + requestPath;
        }

        // Find matching endpoint by method and path pattern
        List<MockEndpoint> candidates = mockEndpointRepository.findByWorkspaceIdAndMethod(workspaceId, method);
        MockEndpoint matchedEndpoint = findMatchingEndpoint(candidates, requestPath);

        // Build headers string for logging
        String headersJson = extractHeaders(servletRequest);

        if (matchedEndpoint == null) {
            // Log unmatched request
            logRequest(workspace, null, method.name(), requestPath, headersJson, requestBody, 404, false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"No matching mock endpoint found\", \"method\": \"" + method
                            + "\", \"path\": \"" + requestPath + "\"}");
        }

        // Simulate latency
        if (matchedEndpoint.getLatencyMs() > 0) {
            try {
                Thread.sleep(matchedEndpoint.getLatencyMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Latency simulation interrupted for endpoint {}", matchedEndpoint.getId());
            }
        }

        String responseBody;
        int statusCode = matchedEndpoint.getStatusCode();

        // Handle stateful logic
        if (Boolean.TRUE.equals(matchedEndpoint.getStatefulEnabled())) {
            responseBody = handleStatefulRequest(matchedEndpoint, workspace, method, requestPath, requestBody);
        } else {
            responseBody = matchedEndpoint.getResponseBody();
        }

        // Log matched request
        logRequest(workspace, matchedEndpoint, method.name(), requestPath, headersJson, requestBody, statusCode, true);

        return ResponseEntity.status(statusCode)
                .header("Content-Type", "application/json")
                .header("X-MockLab-Endpoint-Id", String.valueOf(matchedEndpoint.getId()))
                .body(responseBody);
    }

    /**
     * Matches the incoming request path against endpoint patterns using
     * AntPathMatcher.
     * Supports path variables like /users/{id}, /products/{productId}/reviews, etc.
     */
    private MockEndpoint findMatchingEndpoint(List<MockEndpoint> candidates, String requestPath) {
        // First try exact match
        for (MockEndpoint endpoint : candidates) {
            if (endpoint.getPath().equals(requestPath)) {
                return endpoint;
            }
        }

        // Then try pattern match (AntPathMatcher supports {variable} patterns)
        for (MockEndpoint endpoint : candidates) {
            if (pathMatcher.match(endpoint.getPath(), requestPath)) {
                return endpoint;
            }
        }

        return null;
    }

    /**
     * Handles stateful CRUD logic:
     * - GET: retrieve stored records or return configured response
     * - POST: store the request body as a new record
     * - PUT: merge the request body into an existing record
     * - DELETE: remove a record by key
     */
    private String handleStatefulRequest(MockEndpoint endpoint, Workspace workspace,
            HttpMethodType method, String requestPath, String requestBody) {
        // Use the request path as the record key for stateful storage
        String recordKey = requestPath;

        switch (method) {
            case GET:
                return handleStatefulGet(endpoint, recordKey);

            case POST:
                return handleStatefulPost(endpoint, workspace, recordKey, requestBody);

            case PUT:
            case PATCH:
                return handleStatefulPut(endpoint, workspace, recordKey, requestBody);

            case DELETE:
                return handleStatefulDelete(endpoint, recordKey);

            default:
                return endpoint.getResponseBody();
        }
    }

    private String handleStatefulGet(MockEndpoint endpoint, String recordKey) {
        // Try to get a specific record by key
        Optional<StatefulRecord> record = statefulRecordRepository.findByEndpointIdAndRecordKey(
                endpoint.getId(), recordKey);

        if (record.isPresent()) {
            return record.get().getRecordBody();
        }

        // If no specific record, return all records for this endpoint as an array
        List<StatefulRecord> allRecords = statefulRecordRepository.findByEndpointId(endpoint.getId());
        if (!allRecords.isEmpty()) {
            try {
                ArrayNode arrayNode = objectMapper.createArrayNode();
                for (StatefulRecord sr : allRecords) {
                    JsonNode node = objectMapper.readTree(sr.getRecordBody());
                    arrayNode.add(node);
                }
                return objectMapper.writeValueAsString(arrayNode);
            } catch (Exception e) {
                log.error("Error building stateful response array: {}", e.getMessage());
            }
        }

        // Fall back to configured response
        return endpoint.getResponseBody();
    }

    private String handleStatefulPost(MockEndpoint endpoint, Workspace workspace,
            String recordKey, String requestBody) {
        StatefulRecord record = StatefulRecord.builder()
                .endpoint(endpoint)
                .workspace(workspace)
                .recordKey(recordKey + "/" + UUID.randomUUID().toString().substring(0, 8))
                .recordBody(requestBody)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        record = statefulRecordRepository.save(record);

        try {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("message", "Record created");
            response.put("recordKey", record.getRecordKey());
            if (requestBody != null && !requestBody.isBlank()) {
                JsonNode bodyNode = objectMapper.readTree(requestBody);
                response.set("data", bodyNode);
            }
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"message\": \"Record created\", \"recordKey\": \"" + record.getRecordKey() + "\"}";
        }
    }

    private String handleStatefulPut(MockEndpoint endpoint, Workspace workspace,
            String recordKey, String requestBody) {
        Optional<StatefulRecord> existingOpt = statefulRecordRepository.findByEndpointIdAndRecordKey(
                endpoint.getId(), recordKey);

        if (existingOpt.isPresent()) {
            StatefulRecord existing = existingOpt.get();
            // Merge the request body into the existing record
            String mergedBody = mergeJson(existing.getRecordBody(), requestBody);
            existing.setRecordBody(mergedBody);
            existing.setExpiresAt(LocalDateTime.now().plusHours(24));
            statefulRecordRepository.save(existing);

            return mergedBody;
        } else {
            // Create new record
            StatefulRecord record = StatefulRecord.builder()
                    .endpoint(endpoint)
                    .workspace(workspace)
                    .recordKey(recordKey)
                    .recordBody(requestBody)
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .build();
            statefulRecordRepository.save(record);

            return requestBody;
        }
    }

    private String handleStatefulDelete(MockEndpoint endpoint, String recordKey) {
        Optional<StatefulRecord> record = statefulRecordRepository.findByEndpointIdAndRecordKey(
                endpoint.getId(), recordKey);

        if (record.isPresent()) {
            statefulRecordRepository.delete(record.get());
            return "{\"message\": \"Record deleted\", \"recordKey\": \"" + recordKey + "\"}";
        }

        return "{\"message\": \"Record not found\", \"recordKey\": \"" + recordKey + "\"}";
    }

    /**
     * Deep-merges two JSON strings. Fields from the update JSON overwrite
     * fields in the base JSON. Nested objects are merged recursively.
     */
    private String mergeJson(String baseJson, String updateJson) {
        try {
            JsonNode baseNode = objectMapper.readTree(baseJson);
            JsonNode updateNode = objectMapper.readTree(updateJson);

            if (baseNode.isObject() && updateNode.isObject()) {
                ObjectNode merged = ((ObjectNode) baseNode).deepCopy();
                Iterator<Map.Entry<String, JsonNode>> fields = updateNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    if (merged.has(field.getKey()) && merged.get(field.getKey()).isObject()
                            && field.getValue().isObject()) {
                        // Recursive merge for nested objects
                        String nestedMerged = mergeJson(
                                objectMapper.writeValueAsString(merged.get(field.getKey())),
                                objectMapper.writeValueAsString(field.getValue()));
                        merged.set(field.getKey(), objectMapper.readTree(nestedMerged));
                    } else {
                        merged.set(field.getKey(), field.getValue());
                    }
                }
                return objectMapper.writeValueAsString(merged);
            }

            // If not both objects, update wins
            return updateJson;
        } catch (Exception e) {
            log.error("Error merging JSON: {}", e.getMessage());
            return updateJson;
        }
    }

    private void logRequest(Workspace workspace, MockEndpoint endpoint,
            String method, String path, String headers,
            String body, int responseStatus, boolean matched) {
        try {
            RequestLog log = RequestLog.builder()
                    .workspace(workspace)
                    .endpoint(endpoint)
                    .method(method)
                    .path(path)
                    .requestHeaders(headers)
                    .requestBody(body)
                    .responseStatus(responseStatus)
                    .matched(matched)
                    .build();

            requestLogRepository.save(log);
        } catch (Exception e) {
            MockServerService.log.error("Failed to save request log: {}", e.getMessage());
        }
    }

    private String extractHeaders(HttpServletRequest request) {
        try {
            ObjectNode headersNode = objectMapper.createObjectNode();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headersNode.put(name, request.getHeader(name));
            }
            return objectMapper.writeValueAsString(headersNode);
        } catch (Exception e) {
            return "{}";
        }
    }
}
