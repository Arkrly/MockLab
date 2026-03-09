package io.mocklab.api.repository;

import io.mocklab.api.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    List<RequestLog> findByWorkspaceIdOrderByLoggedAtDesc(Long workspaceId);

    List<RequestLog> findByWorkspaceIdAndMatchedOrderByLoggedAtDesc(Long workspaceId, Boolean matched);

    List<RequestLog> findByEndpointIdOrderByLoggedAtDesc(Long endpointId);
}
