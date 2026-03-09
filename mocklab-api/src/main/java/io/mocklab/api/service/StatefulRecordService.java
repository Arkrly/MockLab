package io.mocklab.api.service;

import io.mocklab.api.entity.StatefulRecord;
import io.mocklab.api.exception.ResourceNotFoundException;
import io.mocklab.api.repository.StatefulRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatefulRecordService {

    private final StatefulRecordRepository statefulRecordRepository;

    public List<StatefulRecord> getRecordsByEndpoint(Long endpointId) {
        return statefulRecordRepository.findByEndpointId(endpointId);
    }

    public List<StatefulRecord> getRecordsByWorkspace(Long workspaceId) {
        return statefulRecordRepository.findByWorkspaceId(workspaceId);
    }

    public Optional<StatefulRecord> getRecordByKey(Long endpointId, String recordKey) {
        return statefulRecordRepository.findByEndpointIdAndRecordKey(endpointId, recordKey);
    }

    @Transactional
    public StatefulRecord saveRecord(StatefulRecord record) {
        record = statefulRecordRepository.save(record);
        log.debug("Stateful record saved: key={} for endpoint={}", record.getRecordKey(), record.getEndpoint().getId());
        return record;
    }

    @Transactional
    public void deleteRecord(Long endpointId, String recordKey) {
        statefulRecordRepository.deleteByEndpointIdAndRecordKey(endpointId, recordKey);
        log.debug("Stateful record deleted: key={} for endpoint={}", recordKey, endpointId);
    }

    @Transactional
    public void deleteRecordById(Long recordId) {
        if (!statefulRecordRepository.existsById(recordId)) {
            throw new ResourceNotFoundException("StatefulRecord", "id", recordId);
        }
        statefulRecordRepository.deleteById(recordId);
    }
}
