package org.example.service;

import com.pgvector.PGvector;
import org.example.domain.Employee;
import org.example.domain.EmployeeBiometric;
import org.example.repository.EmployeeRepository;
import org.example.repository.EmployeeBiometricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeBiometricRepository employeeBiometricRepository;

    @Autowired
    private AIEngineService aiEngineService;

    public Employee createEmployee(Employee employee, String tenantId) {
        employee.setId(UUID.randomUUID());
        employee.setTenantId(tenantId);
        return employeeRepository.save(employee);
    }

    public EmployeeBiometric addBiometric(UUID employeeId, MultipartFile image, String tenantId) {
        String faceEmbeddingString = aiEngineService.getFaceEmbedding(image).block();
        float[] faceEmbedding = parseFaceEmbedding(faceEmbeddingString);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));

        EmployeeBiometric biometric = new EmployeeBiometric();
        biometric.setId(UUID.randomUUID());
        biometric.setTenantId(tenantId);
        biometric.setEmployee(employee);
        biometric.setFaceEmbedding(new PGvector(faceEmbedding));
        return employeeBiometricRepository.save(biometric);
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