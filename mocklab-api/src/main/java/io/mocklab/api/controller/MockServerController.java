package io.mocklab.api.controller;

import io.mocklab.api.enums.HttpMethodType;
import io.mocklab.api.service.MockServerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
@RequestMapping("/mock/{workspaceId}")
@RequiredArgsConstructor
@Slf4j
public class MockServerController {

    private final MockServerService mockServerService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * Catch-all handler for the mock server.
     * Intercepts ALL HTTP methods (GET, POST, PUT, DELETE, PATCH)
     * for any path under /mock/{workspaceId}/**
     */
    @RequestMapping("/**")
    public ResponseEntity<String> handleMockRequest(@PathVariable Long workspaceId,
            @RequestBody(required = false) String body,
            HttpServletRequest request) {

        // Extract the sub-path after /mock/{workspaceId}
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        String requestPath;
        if (fullPath != null && bestMatchPattern != null) {
            requestPath = antPathMatcher.extractPathWithinPattern(bestMatchPattern, fullPath);
        } else {
            // Fallback: manually strip prefix
            String servletPath = request.getServletPath();
            String prefix = "/mock/" + workspaceId;
            requestPath = servletPath.startsWith(prefix)
                    ? servletPath.substring(prefix.length())
                    : servletPath;
        }

        if (requestPath.isEmpty()) {
            requestPath = "/";
        } else if (!requestPath.startsWith("/")) {
            requestPath = "/" + requestPath;
        }

        // Parse HTTP method
        HttpMethodType method;
        try {
            method = HttpMethodType.valueOf(request.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"Unsupported HTTP method: " + request.getMethod() + "\"}");
        }

        log.debug("Mock request: {} {} for workspace {}", method, requestPath, workspaceId);

        return mockServerService.handleMockRequest(workspaceId, requestPath, method, body, request);
    }
}
