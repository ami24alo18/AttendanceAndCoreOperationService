package org.example.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class AttendanceLog {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String tenantId;
    private OffsetDateTime checkInTime;
    private OffsetDateTime checkOutTime;
    private LocalDate logDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}