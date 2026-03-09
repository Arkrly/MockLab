package io.mocklab.api.entity;

import io.mocklab.api.enums.WorkspaceRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workspace_members", uniqueConstraints = @UniqueConstraint(columnNames = { "workspace_id", "user_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private WorkspaceRole role;
}
