package com.library.loan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLibraryPolicyRequest {

    @NotBlank(message = "Policy name is required")
    @Size(max = 100, message = "Policy name must not exceed 100 characters")
    private String policyName;

    @NotBlank(message = "Policy value is required")
    @Size(max = 255, message = "Policy value must not exceed 255 characters")
    private String policyValue;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}