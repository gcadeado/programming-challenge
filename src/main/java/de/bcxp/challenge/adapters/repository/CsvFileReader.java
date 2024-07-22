package de.bcxp.challenge.adapters.repository;

import de.bcxp.challenge.exceptions.FileException;
import de.bcxp.challenge.exceptions.FileFormatException;
import de.bcxp.challenge.exceptions.FileNotFoundException;
import de.bcxp.challenge.ports.IFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public abstract class CsvFileReader<T> implements IFileReader<T> {

    protected abstract T parseLine(String[] values, Map<String, Integer> columnIndexes) throws FileFormatException;

    protected abstract String[] getExpectedHeaders();

    public List<T> readData(String filePath, String delimiter) {
        List<T> records = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String headerLine = br.readLine();
            if (inputStream == null || headerLine == null) {
                throw new FileNotFoundException("The file is empty or not found: " + filePath);
            }

            Map<String, Integer> headerColumnIndexes = getHeaderColumnIndexes(headerLine, delimiter);

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                try {
                    T record = parseLine(values, headerColumnIndexes);
                    records.add(record);
                } catch (FileFormatException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new FileException("Error reading CSV file: " + filePath, e);
        }

        return records;
    }

    protected Map<String, Integer> getHeaderColumnIndexes(String headerLine, String delimiter) {
        validateHeaders(headerLine, delimiter);
        String[] headers = getExpectedHeaders();
        String[] actualHeaders = headerLine.split(delimiter);
        Map<String, Integer> headerColumIndexes = new HashMap<String, Integer>();
        for (String header : headers) {
            headerColumIndexes.put(header, Arrays.asList(actualHeaders).indexOf(header));
        }
        return headerColumIndexes;
    }

    protected void validateHeaders(String headerLine, String delimiter) {
        String[] headers = getExpectedHeaders();
        String[] actualHeaders = headerLine.split(delimiter);
        if (actualHeaders.length < headers.length) {
            throw new FileFormatException("Invalid CSV file format. Expected headers: " + String.join(",", headers));
        }

        if (!Arrays.asList(actualHeaders).containsAll(Arrays.asList(headers))) {
            throw new FileFormatException("Invalid CSV file format. Expected headers: " + String.join(",", headers));
        }
    }
}
