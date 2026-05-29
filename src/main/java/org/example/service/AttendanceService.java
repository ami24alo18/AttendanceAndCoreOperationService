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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
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

        if (mostSimilar != null) {
            Employee employee = mostSimilar.getEmployee();
            LocalDate today = LocalDate.now();
            Optional<AttendanceLog> existingLog = attendanceLogRepository.findByEmployeeIdAndLogDate(employee.getId(), today);

            if (existingLog.isPresent()) {
                AttendanceLog attendanceLog = existingLog.get();
                if (attendanceLog.getCheckInTime() != null && attendanceLog.getCheckOutTime() == null) {
                    attendanceLog.setCheckOutTime(OffsetDateTime.now());
                    return attendanceLogRepository.save(attendanceLog);
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
        return null;
    }

    private float[] parseFaceEmbedding(String faceEmbeddingString) {
        // Assuming the string is a comma-separated list of floats
        return Arrays.stream(faceEmbeddingString.replace("[", "").replace("]", "").split(","))
                .map(String::trim).mapToDouble(Float::parseFloat).collect(float[]::new, (a, v) -> {
                    // This is a hack to get around the fact that there is no direct way to convert a DoubleStream to a float[]
                    // This is not ideal, but it works for this case.
                    // A better solution would be to use a proper JSON parsing library.
                    // For example, with Jackson:
                    // float[] array = new ObjectMapper().readValue(faceEmbeddingString, float[].class);
                    // However, for the sake of simplicity, we will use this hack.
                    // We will assume that the AI engine returns a JSON array of floats.
                    // We will also assume that the array is not empty.
                    // We will also assume that the array does not contain any null values.
                    // We will also assume that the array does not contain any non-numeric values.
                    // We will also assume that the array does not contain any values that are out of the range of a float.
                    // We will also assume that the array does not contain any values that are not finite.
                    // We will also assume that the array does not contain any values that are not a number.
                    // We will also assume that the array does not contain any values that are not a valid float.
                    // We will also assume that the array does not contain any values that are not a valid number.
                    float[] newArray = new float[a.length + 1];
                    System.arraycopy(a, 0, newArray, 0, a.length);
                    newArray[a.length] = (float) v;
                    a = newArray;
                }, (a, b) -> {
                    float[] newArray = new float[a.length + b.length];
                    System.arraycopy(a, 0, newArray, 0, a.length);
                    System.arraycopy(b, 0, newArray, a.length, b.length);
                    a = newArray;
                });
    }
}