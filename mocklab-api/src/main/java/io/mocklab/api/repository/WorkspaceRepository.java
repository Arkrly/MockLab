package io.mocklab.api.repository;

import io.mocklab.api.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    List<Workspace> findByOwnerId(Long ownerId);

    Optional<Workspace> findByApiKey(String apiKey);

    boolean existsByApiKey(String apiKey);
}
