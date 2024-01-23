package com.example.assignment.model;

import lombok.Data;

@Data
public class UriStatistics {
    private String uri;
    private String method;
    private int frequency;
}