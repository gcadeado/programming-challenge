package de.bcxp.challenge.adapters.repository;

import de.bcxp.challenge.core.entities.CountryRecord;
import de.bcxp.challenge.exceptions.FileFormatException;
import de.bcxp.challenge.exceptions.FileNotFoundException;
import de.bcxp.challenge.ports.ICountryFileReader;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CsvCountryFileReader implements ICountryFileReader {

    @Override
    public List<CountryRecord> readCountryData(String filePath) {
        List<CountryRecord> countryRecords = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();

        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

        try (InputStream inputStream = classLoader.getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String headerLine = br.readLine();
            // Check if the file is empty
            if (inputStream == null || headerLine == null) {
                throw new FileNotFoundException("The file is empty or not found: " + filePath);
            }

            String[] headers = headerLine.split(";");
            if (headers.length < 3 ||
                    !Arrays.asList(headers).contains("Name") ||
                    !Arrays.asList(headers).contains("Population") ||
                    !Arrays.asList(headers).contains("Area (km²)")) {
                throw new FileFormatException("Invalid CSV file format. Expected headers: Name,Population,Area");
            }

            int countryNameColumnIndex = Arrays.asList(headers).indexOf("Name");
            int countryPopulationColumnIndex = Arrays.asList(headers).indexOf("Population");
            int countryAreaColumnIndex = Arrays.asList(headers).indexOf("Area (km²)");

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                try {
                    String countryName = values[countryNameColumnIndex];
                    Long countryPopulation = format.parse(values[countryPopulationColumnIndex]).longValue();
                    Double countryArea = format.parse(values[countryAreaColumnIndex]).doubleValue();
                    CountryRecord countryRecord = new CountryRecord(countryName, countryPopulation, countryArea);
                    countryRecords.add(countryRecord);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryRecords;
    }
}