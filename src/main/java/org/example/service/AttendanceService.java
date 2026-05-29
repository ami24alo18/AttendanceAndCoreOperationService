package org.example.service;

import com.pgvector.PGvector;
import org.example.domain.AttendanceLog;
import org.example.domain.Employee;
import org.example.domain.EmployeeBiometric;
import org.example.repository.AttendanceLogRepository;
import org.example.repository.EmployeeBiometricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttendanceService {

    @Autowired
    private AIEngineService aiEngineService;

    @Autowired
    private EmployeeBiometricRepository employeeBiometricRepository;

    @Autowired
    private AttendanceLogRepository attendanceLogRepository;

    public AttendanceLog logAttendance(MultipartFile image, String tenantId) {
        String faceEmbeddingString = aiEngineService.getFaceEmbedding(image).block();
        float[] faceEmbedding = parseFaceEmbedding(faceEmbeddingString);
        EmployeeBiometric mostSimilar = employeeBiometricRepository.findMostSimilar(tenantId, new PGvector(faceEmbedding));

        if (mostSimilar == null) {
            throw new EntityNotFoundException("No matching employee found for the provided biometric data.");
        }

        Employee employee = mostSimilar.getEmployee();
        LocalDate today = LocalDate.now();
        Optional<AttendanceLog> existingLog = attendanceLogRepository.findByEmployeeIdAndLogDate(employee.getId(), today);

        if (existingLog.isPresent()) {
            AttendanceLog attendanceLog = existingLog.get();
            if (attendanceLog.getCheckInTime() != null && attendanceLog.getCheckOutTime() == null) {
                attendanceLog.setCheckOutTime(OffsetDateTime.now());
                return attendanceLogRepository.save(attendanceLog);
            } else {
                throw new IllegalStateException("Employee has already checked in and out for the day.");
            }
        } else {
            AttendanceLog newLog = new AttendanceLog();
            newLog.setId(UUID.randomUUID());
            newLog.setEmployee(employee);
            newLog.setTenantId(tenantId);
            newLog.setLogDate(today);
            newLog.setCheckInTime(OffsetDateTime.now());
            return attendanceLogRepository.save(newLog);
        }
    }

    private float[] parseFaceEmbedding(String faceEmbeddingString) {
        if (faceEmbeddingString == null || faceEmbeddingString.isEmpty()) {
            return new float[0];
        }
        // Assuming the string is a comma-separated list of floats in brackets
        String[] values = faceEmbeddingString.replace("[", "").replace("]", "").split(",");
        float[] faceEmbedding = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            faceEmbedding[i] = Float.parseFloat(values[i].trim());
        }
        return faceEmbedding;
    }
}