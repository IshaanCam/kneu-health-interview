package com.kneu.prescriptions;

import jakarta.validation.constraints.NotNull;

public record CreatePrescriptionRequest(
    @NotNull Long medicationId,
    @NotNull String time, // This would ideally be initialized as a time object
    String name
) {}
// The struct would also include error messages for when these objects are missing
