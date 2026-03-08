package io.mocklab.api.controller;

import io.mocklab.api.dto.request.CreateWorkspaceRequest;
import io.mocklab.api.dto.response.WorkspaceResponse;
import io.mocklab.api.entity.User;
import io.mocklab.api.enums.WorkspaceRole;
import io.mocklab.api.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        WorkspaceResponse response = workspaceService.createWorkspace(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<WorkspaceResponse> workspaces = workspaceService.getWorkspacesByOwner(currentUser.getId());
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long id) {
        WorkspaceResponse workspace = workspaceService.getWorkspaceById(id);
        return ResponseEntity.ok(workspace);
    }

    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<WorkspaceResponse> addMember(@PathVariable Long workspaceId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "CONSUMER") WorkspaceRole role) {
        WorkspaceResponse response = workspaceService.addMember(workspaceId, userId, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workspaceId}/regenerate-key")
    public ResponseEntity<WorkspaceResponse> regenerateApiKey(@PathVariable Long workspaceId) {
        WorkspaceResponse response = workspaceService.regenerateApiKey(workspaceId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }
}
