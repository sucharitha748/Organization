package com.stackly.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    private String organizationCode;
    private CodeMode codeMode;
    private String organizationName;
    private String organizationType;
    private String industry;
    private String companySize;
    private String country;
    private String stateProvince;
    private String city;
    private String timeZone;
    private String organizationLogo;
    private LocalDateTime createdAt;
}
