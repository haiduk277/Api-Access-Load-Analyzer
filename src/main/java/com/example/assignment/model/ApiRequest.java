package com.example.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequest {
    private String ip;
    private String data;
    private String requestMethod;
    private String uri;
    private int requestStatus;
}
