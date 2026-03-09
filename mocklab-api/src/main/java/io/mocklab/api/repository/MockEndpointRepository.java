package io.mocklab.api.repository;

import io.mocklab.api.entity.MockEndpoint;
import io.mocklab.api.enums.HttpMethodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockEndpointRepository extends JpaRepository<MockEndpoint, Long> {

    List<MockEndpoint> findByWorkspaceId(Long workspaceId);

    @Query("SELECT e FROM MockEndpoint e WHERE e.workspace.id = :workspaceId AND e.method = :method")
    List<MockEndpoint> findByWorkspaceIdAndMethod(@Param("workspaceId") Long workspaceId,
            @Param("method") HttpMethodType method);

    @Query("SELECT e FROM MockEndpoint e WHERE e.workspace.id = :workspaceId AND e.method = :method AND e.path = :path")
    Optional<MockEndpoint> findByWorkspaceIdAndMethodAndPath(@Param("workspaceId") Long workspaceId,
            @Param("method") HttpMethodType method,
            @Param("path") String path);

    void deleteByIdAndWorkspaceId(Long id, Long workspaceId);
}
