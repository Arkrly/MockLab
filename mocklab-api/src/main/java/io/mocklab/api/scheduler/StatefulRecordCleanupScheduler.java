package io.mocklab.api.scheduler;

import io.mocklab.api.repository.StatefulRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatefulRecordCleanupScheduler {

    private final StatefulRecordRepository statefulRecordRepository;

    /**
     * Runs every 60 seconds to clean up expired stateful records.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredRecords() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = statefulRecordRepository.deleteExpiredRecords(now);
        if (deletedCount > 0) {
            log.info("Cleaned up {} expired stateful records", deletedCount);
        }
    }
}
