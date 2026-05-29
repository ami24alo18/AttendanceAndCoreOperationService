package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.domain.Employee;
import org.example.domain.EmployeeBiometric;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create a new employee")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-ID", required = true, schema = @Schema(type = "string"))
    public Employee createEmployee(@RequestBody Employee employee, @RequestHeader("X-Tenant-ID") String tenantId) {
        return employeeService.createEmployee(employee, tenantId);
    }

    @PostMapping(value = "/{employeeId}/biometric", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add biometric data for an employee")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-ID", required = true, schema = @Schema(type = "string"))
    @ApiResponse(responseCode = "200", description = "Biometric data added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeBiometric.class)))
    public EmployeeBiometric addBiometric(
            @PathVariable UUID employeeId,
            @Parameter(description = "Image file for biometric data", required = true) @RequestPart("image") MultipartFile image,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return employeeService.addBiometric(employeeId, image, tenantId);
    }
}