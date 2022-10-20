package com.person.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {
    private int statusCode;
    private Object body;
    @JsonProperty( "isBase64Encoded")
    private boolean isBase64Encoded;
}
