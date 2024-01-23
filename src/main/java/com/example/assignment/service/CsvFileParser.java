package com.example.assignment.service;

import com.example.assignment.model.ApiRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvFileParser {

    private static List<ApiRequest> storedApiRequests = new ArrayList<>();

    public static List<ApiRequest> parseCsv(InputStream inputStream) throws IOException {
        List<ApiRequest> apiRequests = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ApiRequest apiRequest = parseCsvLine(line);
                if (apiRequest != null) {
                    apiRequests.add(apiRequest);
                    storedApiRequests.add(apiRequest);
                }
            }
        }

        return apiRequests;
    }

    public static List<ApiRequest> loadStoredApiRequests() {
        return new ArrayList<>(storedApiRequests);
    }

    private static ApiRequest parseCsvLine(String line) {
        String[] fields = line.split(";");
        if (fields.length == 5) {
            ApiRequest apiRequest = new ApiRequest();
            apiRequest.setIp(fields[0]);
            apiRequest.setData(fields[1]);
            apiRequest.setRequestMethod(fields[2]);
            apiRequest.setUri(fields[3]);

            try {
                int requestStatus = Integer.parseInt(fields[4]);
                apiRequest.setRequestStatus(requestStatus);
            } catch (NumberFormatException e) {
                return null; // Return null for invalid status
            }

            return apiRequest;
        }
        return null;
    }
}
