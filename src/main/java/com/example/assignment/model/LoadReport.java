package com.example.assignment.model;

import lombok.Data;

import java.util.List;

@Data
public class LoadReport {
    private List<UriStatistics> topNUris;
    private List<RequestStatistics> requestStatistics;
    private Counters counters;
}