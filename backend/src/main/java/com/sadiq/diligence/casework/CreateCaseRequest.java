package com.sadiq.diligence.casework;

import jakarta.validation.constraints.NotBlank;

public record CreateCaseRequest(
    @NotBlank(message = "supplierName is required")
    String supplierName,
    
    @NotBlank(message = "requestedBy is required")
    String requestedBy
) { }
