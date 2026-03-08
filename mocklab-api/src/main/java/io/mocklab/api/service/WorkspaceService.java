package io.mocklab.api.service;

import io.mocklab.api.dto.request.CreateWorkspaceRequest;
import io.mocklab.api.dto.response.WorkspaceResponse;
import io.mocklab.api.entity.User;
import io.mocklab.api.entity.Workspace;
import io.mocklab.api.entity.WorkspaceMember;
import io.mocklab.api.enums.WorkspaceRole;
import io.mocklab.api.exception.ResourceNotFoundException;
import io.mocklab.api.repository.UserRepository;
import io.mocklab.api.repository.WorkspaceMemberRepository;
import io.mocklab.api.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, User owner) {
        String apiKey = generateUniqueApiKey();

        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .owner(owner)
                .apiKey(apiKey)
                .build();

        workspace = workspaceRepository.save(workspace);

        // Owner is automatically an ADMIN member
        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .workspace(workspace)
                .user(owner)
                .role(WorkspaceRole.ADMIN)
                .build();
        workspaceMemberRepository.save(ownerMember);

        log.info("Workspace created: '{}' by user {}", workspace.getName(), owner.getEmail());

        return toResponse(workspace);
    }

    public List<WorkspaceResponse> getWorkspacesByOwner(Long ownerId) {
        return workspaceRepository.findByOwnerId(ownerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkspaceResponse getWorkspaceById(Long id) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", id));
        return toResponse(workspace);
    }

    @Transactional
    public WorkspaceResponse addMember(Long workspaceId, Long userId, WorkspaceRole role) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
            throw new IllegalArgumentException("User is already a member of this workspace");
        }

        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .user(user)
                .role(role)
                .build();
        workspaceMemberRepository.save(member);

        log.info("User {} added to workspace '{}' as {}", user.getEmail(), workspace.getName(), role);

        return toResponse(workspace);
    }

    @Transactional
    public WorkspaceResponse regenerateApiKey(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        workspace.setApiKey(generateUniqueApiKey());
        workspace = workspaceRepository.save(workspace);

        log.info("API key regenerated for workspace '{}'", workspace.getName());

        return toResponse(workspace);
    }

    @Transactional
    public void deleteWorkspace(Long workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new ResourceNotFoundException("Workspace", "id", workspaceId);
        }
        workspaceRepository.deleteById(workspaceId);
        log.info("Workspace deleted: id={}", workspaceId);
    }

    private String generateUniqueApiKey() {
        String apiKey;
        do {
            apiKey = "ml_" + UUID.randomUUID().toString().replace("-", "");
        } while (workspaceRepository.existsByApiKey(apiKey));
        return apiKey;
    }

    private WorkspaceResponse toResponse(Workspace workspace) {
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .apiKey(workspace.getApiKey())
                .ownerId(workspace.getOwner().getId())
                .ownerName(workspace.getOwner().getName())
                .createdAt(workspace.getCreatedAt())
                .build();
    }
}
