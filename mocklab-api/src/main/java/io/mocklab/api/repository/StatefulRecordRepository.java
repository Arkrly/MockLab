package io.mocklab.api.repository;

import io.mocklab.api.entity.StatefulRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatefulRecordRepository extends JpaRepository<StatefulRecord, Long> {

    List<StatefulRecord> findByEndpointId(Long endpointId);

    List<StatefulRecord> findByWorkspaceId(Long workspaceId);

    Optional<StatefulRecord> findByEndpointIdAndRecordKey(Long endpointId, String recordKey);

    @Modifying
    @Query("DELETE FROM StatefulRecord sr WHERE sr.expiresAt IS NOT NULL AND sr.expiresAt < :now")
    int deleteExpiredRecords(@Param("now") LocalDateTime now);

    void deleteByEndpointIdAndRecordKey(Long endpointId, String recordKey);
}
