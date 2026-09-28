package com.stackly.organization.dto;

public record CodeAvailabilityResponse(String organizationCode, boolean available, String message) {
}
