package com.stackly.organization.service;

import com.stackly.organization.dto.CodeAvailabilityResponse;
import com.stackly.organization.dto.CodeGenerateResponse;
import com.stackly.organization.dto.OrganizationRequest;
import com.stackly.organization.dto.OrganizationResponse;
import com.stackly.organization.exception.InvalidOrganizationCodeException;
import com.stackly.organization.exception.OrganizationAlreadyExistsException;
import com.stackly.organization.exception.OrganizationNotFoundException;
import com.stackly.organization.model.CodeMode;
import com.stackly.organization.model.Organization;
import com.stackly.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class OrganizationService {

    private static final int CODE_MIN_LENGTH = 3;
    private static final int CODE_MAX_LENGTH = 30;
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9]+(-[A-Z0-9]+)*$");

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public OrganizationResponse createOrganization(OrganizationRequest request) {
        CodeMode mode = resolveMode(request);

        Organization organization = new Organization(
                null,
                mode,
                request.getOrganizationName(),
                request.getOrganizationType(),
                request.getIndustry(),
                request.getCompanySize(),
                request.getCountry(),
                request.getStateProvince(),
                request.getCity(),
                request.getTimeZone(),
                request.getOrganizationLogo(),
                LocalDateTime.now()
        );

        if (mode == CodeMode.MANUAL) {
            String code = validateManualCode(request.getOrganizationCode());
            organization.setOrganizationCode(code);
            if (!organizationRepository.saveIfAbsent(organization)) {
                throw new OrganizationAlreadyExistsException(
                        "Organization already exists with code: " + code);
            }
        } else {
            saveWithGeneratedCode(organization);
        }

        return toResponse(organization);
    }

    public OrganizationResponse getOrganizationByCode(String code) {
        Organization organization = organizationRepository.findByCode(code)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "Organization not found with code: " + code));
        return toResponse(organization);
    }

    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** For the UI: live preview of the auto-generated code (does not reserve it). */
    public CodeGenerateResponse previewGeneratedCode(String organizationName) {
        String base = generateBaseCode(organizationName);
        String candidate = base;
        for (int i = 2; organizationRepository.existsByCode(candidate); i++) {
            candidate = withSuffix(base, i);
        }
        return new CodeGenerateResponse(candidate);
    }

    /** For the UI: check a manually entered code for format and availability. */
    public CodeAvailabilityResponse checkCodeAvailability(String organizationCode) {
        String normalized = organizationCode == null ? "" : organizationCode.trim().toUpperCase();
        try {
            String code = validateManualCode(organizationCode);
            boolean taken = organizationRepository.existsByCode(code);
            return new CodeAvailabilityResponse(code, !taken,
                    taken ? "Code already in use" : "Code is available");
        } catch (InvalidOrganizationCodeException ex) {
            return new CodeAvailabilityResponse(normalized, false, ex.getMessage());
        }
    }

    // ---- helpers ----

    private CodeMode resolveMode(OrganizationRequest request) {
        if (request.getCodeMode() != null) {
            return request.getCodeMode();
        }
        String code = request.getOrganizationCode();
        return (code == null || code.isBlank()) ? CodeMode.AUTO : CodeMode.MANUAL;
    }

    private void saveWithGeneratedCode(Organization organization) {
        String base = generateBaseCode(organization.getOrganizationName());
        String candidate = base;
        int suffix = 2;
        organization.setOrganizationCode(candidate);
        while (!organizationRepository.saveIfAbsent(organization)) {
            candidate = withSuffix(base, suffix++);
            organization.setOrganizationCode(candidate);
        }
    }

    private String generateBaseCode(String organizationName) {
        String code = organizationName == null ? "" : organizationName.trim().toUpperCase()
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (code.length() > CODE_MAX_LENGTH) {
            code = code.substring(0, CODE_MAX_LENGTH).replaceAll("-+$", "");
        }
        if (code.isEmpty()) {
            throw new InvalidOrganizationCodeException(
                    "Cannot auto-generate a code from this organization name; use MANUAL mode");
        }
        return code;
    }

    private String withSuffix(String base, int n) {
        String suffix = "-" + n;
        String trimmed = base.length() + suffix.length() > CODE_MAX_LENGTH
                ? base.substring(0, CODE_MAX_LENGTH - suffix.length()).replaceAll("-+$", "")
                : base;
        return trimmed + suffix;
    }

    private String validateManualCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new InvalidOrganizationCodeException(
                    "Organization Code is required when codeMode is MANUAL");
        }
        String code = rawCode.trim().toUpperCase();
        if (code.length() < CODE_MIN_LENGTH || code.length() > CODE_MAX_LENGTH) {
            throw new InvalidOrganizationCodeException(
                    "Organization Code must be " + CODE_MIN_LENGTH + "-" + CODE_MAX_LENGTH + " characters");
        }
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new InvalidOrganizationCodeException(
                    "Organization Code may contain only letters, digits and single hyphens (e.g. ACME-CORP)");
        }
        return code;
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getOrganizationCode(),
                organization.getCodeMode(),
                organization.getOrganizationName(),
                organization.getOrganizationType(),
                organization.getIndustry(),
                organization.getCompanySize(),
                organization.getCountry(),
                organization.getStateProvince(),
                organization.getCity(),
                organization.getTimeZone(),
                organization.getOrganizationLogo(),
                organization.getCreatedAt()
        );
    }
}
