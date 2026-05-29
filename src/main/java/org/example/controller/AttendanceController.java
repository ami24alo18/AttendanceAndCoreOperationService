package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.domain.AttendanceLog;
import org.example.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping(value = "/log", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Log attendance using biometric data")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-ID", required = true, schema = @Schema(type = "string"))
    @ApiResponse(responseCode = "200", description = "Attendance logged successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceLog.class)))
    public AttendanceLog logAttendance(
            @Parameter(description = "Image file for biometric data", required = true) @RequestPart("image") MultipartFile image,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return attendanceService.logAttendance(image, tenantId);
    }
}