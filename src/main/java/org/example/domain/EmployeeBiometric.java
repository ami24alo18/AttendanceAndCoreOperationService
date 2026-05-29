package org.example.domain;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class EmployeeBiometric {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String tenantId;

    @Type(com.pgvector.hibernate.VectorType.class)
    @Column(columnDefinition = "vector(512)")
    private PGvector faceEmbedding;

    private boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}