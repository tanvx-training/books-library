package com.library.loan.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryPolicySearchRequest {

    private Integer page = 0;

    private Integer size = 10;

    private String policyName;

    private String sortBy = "policyName";

    private String order = "asc";
}