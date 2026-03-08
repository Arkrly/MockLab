package io.mocklab.api.entity;

import io.mocklab.api.enums.HttpMethodType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mock_endpoints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private HttpMethodType method;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "status_code", nullable = false)
    @Builder.Default
    private Integer statusCode = 200;

    @Column(name = "latency_ms", nullable = false)
    @Builder.Default
    private Integer latencyMs = 0;

    @Column(name = "stateful_enabled", nullable = false)
    @Builder.Default
    private Boolean statefulEnabled = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StatefulRecord> statefulRecords = new ArrayList<>();
}
