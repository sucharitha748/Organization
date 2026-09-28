package com.stackly.organization.dto;

import com.stackly.organization.model.CodeMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizationRequest {

    @NotBlank(message = "Organization Name is required")
    private String organizationName;

    /** AUTO = generate from organizationName; MANUAL = use organizationCode as entered.
     *  If omitted: MANUAL when organizationCode is provided, otherwise AUTO. */
    private CodeMode codeMode;

    private String organizationCode;

    @NotBlank(message = "Organization Type is required")
    private String organizationType;

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotBlank(message = "Company Size is required")
    private String companySize;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "State / Province is required")
    private String stateProvince;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Time Zone is required")
    private String timeZone;

    private String organizationLogo;
}
