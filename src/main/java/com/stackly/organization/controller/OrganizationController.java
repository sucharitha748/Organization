package com.stackly.organization.controller;

import com.stackly.organization.dto.CodeAvailabilityResponse;
import com.stackly.organization.dto.CodeGenerateResponse;
import com.stackly.organization.dto.OrganizationRequest;
import com.stackly.organization.dto.OrganizationResponse;
import com.stackly.organization.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody OrganizationRequest request) {
        OrganizationResponse response = organizationService.createOrganization(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /** Auto mode: preview the code generated from the organization name. */
    @GetMapping("/code/generate")
    public ResponseEntity<CodeGenerateResponse> generateCode(
            @RequestParam(defaultValue = "") String organizationName) {
        return ResponseEntity.ok(organizationService.previewGeneratedCode(organizationName));
    }

    /** Manual mode: validate format and availability of an entered code. */
    @GetMapping("/code/check")
    public ResponseEntity<CodeAvailabilityResponse> checkCode(
            @RequestParam(defaultValue = "") String organizationCode) {
        return ResponseEntity.ok(organizationService.checkCodeAvailability(organizationCode));
    }

    @GetMapping("/{organizationCode}")
    public ResponseEntity<OrganizationResponse> getOrganization(
            @PathVariable String organizationCode) {
        return ResponseEntity.ok(organizationService.getOrganizationByCode(organizationCode));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }
}
