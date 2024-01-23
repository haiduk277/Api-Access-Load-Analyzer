package com.example.assignment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.assignment.model.ApiRequest;
import com.example.assignment.model.Counters;
import com.example.assignment.model.LoadReport;
import com.example.assignment.model.RequestStatistics;
import com.example.assignment.model.UriStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

class LoadAnalyzerServiceTest {

    private LoadAnalyzerService loadAnalyzerService;
    private CsvFileParser csvFileParserMock;
    private Logger loggerMock;

    @BeforeEach
    void setUp() {
        csvFileParserMock = mock(CsvFileParser.class);
        loggerMock = mock(Logger.class);
        loadAnalyzerService = new LoadAnalyzerService();
        loadAnalyzerService.setCsvFileParser(csvFileParserMock);
        loadAnalyzerService.setLogger(loggerMock);
    }

    @Test
    void processCsvData_ValidApiRequest_ProcessesValidApiRequest() {
        // Arrange
        ApiRequest validApiRequest = createValidApiRequest("192.168.2.212", "28/07/2006:10:27:10-0300", "GET", "/user/try/", 200);
        List<ApiRequest> apiRequests = List.of(validApiRequest);

        // Act
        loadAnalyzerService.processCsvData(apiRequests);

        // Assert
        verify(loggerMock).info("Processing valid API request: {}", validApiRequest);
    }

    @Test
    void processCsvData_InvalidApiRequest_HandlesInvalidApiRequest() {
        // Arrange
        ApiRequest invalidApiRequest = new ApiRequest();
        List<ApiRequest> apiRequests = List.of(invalidApiRequest);

        // Act
        loadAnalyzerService.processCsvData(apiRequests);

        // Assert
        verify(loggerMock).warn("Invalid API request: {}", invalidApiRequest);
    }

    @Test
    void generateLoadReport_ValidApiRequests_ReturnsLoadReport() {
        // Arrange
        ApiRequest validApiRequest1 = createValidApiRequest("192.168.2.212", "28/07/2006:10:27:10-0300", "GET", "/user/try/", 200);
        ApiRequest validApiRequest2 = createValidApiRequest("192.168.2.213", "28/07/2006:10:28:10-0300", "POST", "/user/create/", 201);
        List<ApiRequest> apiRequests = List.of(validApiRequest1, validApiRequest2);

        // Act
        LoadReport loadReport = loadAnalyzerService.generateLoadReport(apiRequests, 5);

        // Assert
        assertNotNull(loadReport);
        assertLoadReportCounts(loadReport.getCounters(), 2, 2);
        assertNotEmptyAndInstanceOf(loadReport.getTopNUris(), UriStatistics.class);
        assertNotEmptyAndInstanceOf(loadReport.getRequestStatistics(), RequestStatistics.class);
    }

    @Test
    void getCounters_ValidApiRequests_ReturnsCounters() {
        // Arrange
        ApiRequest validApiRequest1 = createValidApiRequest("192.168.2.212", "28/07/2006:10:27:10-0300", "GET", "/user/try/", 200);
        ApiRequest validApiRequest2 = createValidApiRequest("192.168.2.213", "28/07/2006:10:28:10-0300", "POST", "/user/create/", 201);
        List<ApiRequest> apiRequests = List.of(validApiRequest1, validApiRequest2);

        // Act
        Counters counters = loadAnalyzerService.getCounters(apiRequests);

        // Assert
        assertNotNull(counters);
        assertLoadReportCounts(counters, 2, 2);
        assertTrue(counters.getProcessedTimeInSeconds() >= 0);
    }

    // Helper method to create a valid ApiRequest
    private ApiRequest createValidApiRequest(String ip, String timestamp, String method, String uri, int status) {
        return ApiRequest.builder()
                .ip(ip)
                .data(timestamp)
                .requestMethod(method)
                .uri(uri)
                .requestStatus(status)
                .build();
    }

    // Helper method to assert counters in LoadReport
    private void assertLoadReportCounts(Counters counters, int totalRows, int validRows) {
        assertNotNull(counters);
        assertEquals(totalRows, counters.getTotalRows());
        assertEquals(validRows, counters.getValidRows());
    }

    // Helper method to assert that a list is not empty and contains instances of a specified class
    private <T> void assertNotEmptyAndInstanceOf(List<T> list, Class<T> clazz) {
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.get(0).getClass().isAssignableFrom(clazz));
    }
}
