package com.library.loan.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FineSearchRequest {

    private int page = 0;

    private int size = 10;

    private String borrowingPublicId;

    private String userPublicId;

    private String status;

    private String sortBy = "createdAt";

    private String order = "desc";
}