package de.bcxp.challenge;

import de.bcxp.challenge.adapters.csv.CsvCountryFileReader;
import de.bcxp.challenge.configuration.AppConfig;
import de.bcxp.challenge.core.entities.CountryRecord;
import de.bcxp.challenge.exceptions.CsvFileFormatException;
import de.bcxp.challenge.ports.IFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class CsvCountriesFileReaderTest {
    @Test
    public void testReadCountryData_InvalidLocale() {
        InputStream inputStream =
                AppConfig.class.getClassLoader().getResourceAsStream("de/bcxp/challenge/test_countries.csv");
        IFileReader<CountryRecord> countryReader =
                new CsvCountryFileReader.Builder(inputStream).withSeparator(';')
                        .withLocale(Locale.US.getLanguage())
                        .build();
        List<CountryRecord> countryRecords = countryReader.readData();
        
        // Assertions
        assertNotEquals(4036355, countryRecords.get(1).getPopulation(), 0.01);
    }

    @Test
    public void testReadCountryData_InvalidSeparator() {
        InputStream inputStream =
                AppConfig.class.getClassLoader().getResourceAsStream("de/bcxp/challenge/test_countries.csv");
        IFileReader<CountryRecord> countryReader =
                new CsvCountryFileReader.Builder(inputStream).withSeparator(',')
                        .withLocale(Locale.US.getLanguage())
                        .build();

        Executable executable = countryReader::readData;

        CsvFileFormatException exception = assertThrows(CsvFileFormatException.class, executable);
        assertEquals("Error reading CSV file", exception.getMessage());
    }

    @Test
    public void testReadCountryData_InvalidLine() {
        InputStream inputStream =
                AppConfig.class.getClassLoader().getResourceAsStream("de/bcxp/challenge/invalid_line_countries.csv");
        IFileReader<CountryRecord> countryReader =
                new CsvCountryFileReader.Builder(inputStream).withSeparator(';').withLocale(
                        Locale.GERMANY.getLanguage()).build();

        Executable executable = countryReader::readData;

        CsvFileFormatException exception = assertThrows(CsvFileFormatException.class, executable);
        assertEquals("Error reading CSV file", exception.getMessage());
    }

    @Test
    public void testReadCountryData_ValidFile() {
        InputStream inputStream =
                AppConfig.class.getClassLoader().getResourceAsStream("de/bcxp/challenge/test_countries.csv");
        IFileReader<CountryRecord> countryReader =
                new CsvCountryFileReader.Builder(inputStream).withSeparator(';').withLocale(
                        Locale.GERMANY.getLanguage()).build();
        List<CountryRecord> countryRecords = countryReader.readData();

        // Assertions
        assertEquals(3, countryRecords.size());
        assertEquals("Austria", countryRecords.get(0).getName());
        assertEquals(83855, countryRecords.get(0).getArea(), 0.01);
        assertEquals(4036355, countryRecords.get(1).getPopulation(), 0.01);
    }
}