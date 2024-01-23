package com.example.assignment.service;

import com.example.assignment.TestUtils;
import com.example.assignment.model.ApiRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CsvFileParserTest {

    @Test
    void testParseCsv() throws IOException {
        String csvContent = "192.168.2.212;28/07/2006:10:27:10-0300;GET;/user/try/;200\n" +
                "192.168.2.212;28/07/2006:10:22:04-0300;GET;/;200\n" +
                "192.168.2.220;28/07/2006:10:25:04-0300;PUT;/save/;200\n" +
                "192.168.2.111;28/07/2006:10:25:04-0300;PUT;/save/;403";

        try (InputStream inputStream = TestUtils.convertStringToInputStream(csvContent)) {
            List<ApiRequest> apiRequests = CsvFileParser.parseCsv(inputStream);

            System.out.println("Actual result count: " + apiRequests.size());
            apiRequests.forEach(apiRequest -> System.out.println(apiRequest));

            assertNotNull(apiRequests);

            List<ApiRequest> validRequests = apiRequests.stream()
                    .filter(apiRequest -> apiRequest.getRequestStatus() == 200)
                    .collect(Collectors.toList());

            assertEquals(3, validRequests.size());
        }
    }


    @Test
    void testLoadStoredApiRequests() {
        List<ApiRequest> storedApiRequests = CsvFileParser.loadStoredApiRequests();

        assertNotNull(storedApiRequests);
    }

    @Test
    void testInvalidCsvLine() {
        String invalidCsvLine = "invalid,line,format";

        try (InputStream inputStream = TestUtils.convertStringToInputStream(invalidCsvLine)) {
            List<ApiRequest> apiRequests = CsvFileParser.parseCsv(inputStream);

            assertNotNull(apiRequests);
            assertEquals(0, apiRequests.size());
        } catch (IOException e) {
        }
    }

    @Test
    void testEmptyCsv() throws IOException {
        String emptyCsvContent = "";

        try (InputStream inputStream = TestUtils.convertStringToInputStream(emptyCsvContent)) {
            List<ApiRequest> apiRequests = CsvFileParser.parseCsv(inputStream);

            assertNotNull(apiRequests);
            assertEquals(0, apiRequests.size());
        }
    }

    @Test
    void testCsvWithInvalidStatus() throws IOException {
        String csvWithInvalidStatus = "192.168.2.212;28/07/2006:10:27:10-0300;GET;/user/try/;InvalidStatus\n";

        try (InputStream inputStream = TestUtils.convertStringToInputStream(csvWithInvalidStatus)) {
            List<ApiRequest> apiRequests = CsvFileParser.parseCsv(inputStream);

            assertNotNull(apiRequests);
            assertEquals(0, apiRequests.size());
        }
    }

    @Test
    void testCsvWithMissingFields() throws IOException {
        String csvWithMissingFields = "192.168.2.212;28/07/2006:10:27:10-0300;GET;/user/try/\n";

        try (InputStream inputStream = TestUtils.convertStringToInputStream(csvWithMissingFields)) {
            List<ApiRequest> apiRequests = CsvFileParser.parseCsv(inputStream);

            assertNotNull(apiRequests);
            assertEquals(0, apiRequests.size());
        }
    }

    @Test
    void testCsvWithExtraFields() throws IOException {
        String csvWithExtraFields = "192.168.2.212;28/07/2006:10:27:10-0300;GET;/user/try/;200;ExtraField\n";

        try (InputStream inputStream = TestUtils.convertStringToInputStream(csvWithExtraFields)) {
            List<ApiRequest> apiRequests = CsvFileParser.parseCsv(inputStream);

            assertNotNull(apiRequests);
            assertEquals(0, apiRequests.size());
        }
    }
}
