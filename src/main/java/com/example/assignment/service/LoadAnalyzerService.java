package com.example.assignment.service;

import com.example.assignment.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoadAnalyzerService {

    private static Logger logger = LoggerFactory.getLogger(LoadAnalyzerService.class);
    private CsvFileParser csvFileParser;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setCsvFileParser(CsvFileParser csvFileParser) {
        this.csvFileParser = csvFileParser;
    }

    public void processCsvData(List<ApiRequest> apiRequests) {
        for (ApiRequest apiRequest : apiRequests) {
            if (isValid(apiRequest)) {
                processValidApiRequest(apiRequest);
            } else {
                handleInvalidApiRequest(apiRequest);
            }
        }
    }

    private void handleInvalidApiRequest(ApiRequest apiRequest) {
        logger.warn("Invalid API request: {}", apiRequest);
    }

    public LoadReport generateLoadReport(List<ApiRequest> apiRequests, int topN) {
        LoadReport loadReport = new LoadReport();
        loadReport.setTopNUris(getTopNUris(apiRequests, topN));
        loadReport.setRequestStatistics(getRequestStatistics(apiRequests));
        loadReport.setCounters(getCounters(apiRequests));

        return loadReport;
    }

    private List<UriStatistics> getTopNUris(List<ApiRequest> apiRequests, int topN) {
        Map<String, Long> uriFrequencyMap = apiRequests.stream()
                .collect(Collectors.groupingBy(ApiRequest::getUri, Collectors.counting()));

        return uriFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> {
                    UriStatistics uriStatistics = new UriStatistics();
                    uriStatistics.setUri(entry.getKey());
                    uriStatistics.setFrequency(entry.getValue().intValue());
                    uriStatistics.setMethod(getMostUsedMethod(apiRequests, entry.getKey()));
                    return uriStatistics;
                })
                .collect(Collectors.toList());
    }

    private String getMostUsedMethod(List<ApiRequest> apiRequests, String uri) {
        Map<String, Long> methodFrequencyMap = apiRequests.stream()
                .filter(request -> request.getUri().equals(uri))
                .collect(Collectors.groupingBy(ApiRequest::getRequestMethod, Collectors.counting()));

        return methodFrequencyMap.entrySet().stream()
                .max(Map.Entry.<String, Long>comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private List<RequestStatistics> getRequestStatistics(List<ApiRequest> apiRequests) {
        Map<String, Long> timestampRequestCountMap = apiRequests.stream()
                .collect(Collectors.groupingBy(ApiRequest::getData, Collectors.counting()));

        return timestampRequestCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    RequestStatistics requestStatistics = new RequestStatistics();
                    requestStatistics.setTimestamp(entry.getKey());
                    requestStatistics.setRequestCount(entry.getValue().intValue());
                    return requestStatistics;
                })
                .collect(Collectors.toList());
    }

    protected Counters getCounters(List<ApiRequest> apiRequests) {
        Counters counters = new Counters();
        long startTime = System.currentTimeMillis();

        counters.setTotalRows(apiRequests.size());
        counters.setValidRows((int) apiRequests.stream().filter(this::isValid).count());

        long endTime = System.currentTimeMillis();
        long elapsedTimeInSeconds = (endTime - startTime) / 1000;
        counters.setProcessedTimeInSeconds((int) elapsedTimeInSeconds);

        return counters;
    }

    private void processValidApiRequest(ApiRequest apiRequest) {
        logger.info("Processing valid API request: {}", apiRequest);
    }

    private boolean isValid(ApiRequest apiRequest) {
        return apiRequest != null
                && isValidIp(apiRequest.getIp())
                && isValidTimestamp(apiRequest.getData());
    }

    private boolean isValidIp(String ip) {
        return ip != null && ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    }

    private boolean isValidTimestamp(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ssZ");
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(timestamp);
            return parsedDate != null;
        } catch (ParseException e) {
            return false;
        }
    }
}
