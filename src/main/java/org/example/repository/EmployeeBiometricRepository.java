package org.example.repository;

import com.pgvector.PGvector;
import org.example.domain.EmployeeBiometric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeBiometricRepository extends JpaRepository<EmployeeBiometric, UUID> {

    @Query(value = "SELECT * FROM employee_biometric WHERE tenant_id = :tenantId ORDER BY face_embedding <=> :faceEmbedding LIMIT 1", nativeQuery = true)
    EmployeeBiometric findMostSimilar(@Param("tenantId") String tenantId, @Param("faceEmbedding") PGvector faceEmbedding);
}