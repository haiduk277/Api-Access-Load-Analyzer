package com.example.assignment.controller;

import com.example.assignment.model.ApiRequest;
import com.example.assignment.model.LoadReport;
import com.example.assignment.service.CsvFileParser;
import com.example.assignment.service.LoadAnalyzerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private LoadAnalyzerService loadAnalyzerService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide a CSV file");
        }

        try {
            List<ApiRequest> apiRequests = CsvFileParser.parseCsv(file.getInputStream());
            loadAnalyzerService.processCsvData(apiRequests);
            return ResponseEntity.status(HttpStatus.CREATED).body("CSV file uploaded and processed successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing CSV file");
        }
    }

    @GetMapping("/load-report")
    @Operation(summary = "Get a load report based on top N API requests")
    public ResponseEntity<LoadReport> getLoadReport(@RequestParam("topN") int topN) {
        List<ApiRequest> apiRequests = CsvFileParser.loadStoredApiRequests();
        LoadReport loadReport = loadAnalyzerService.generateLoadReport(apiRequests, topN);
        return ResponseEntity.ok(loadReport);
    }
}
