package de.bcxp.challenge;

import de.bcxp.challenge.adapters.repository.CsvCountryFileReader;
import de.bcxp.challenge.core.entities.CountryRecord;
import de.bcxp.challenge.exceptions.FileFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class CsvCountriesFileReaderTest {

    private CsvCountryFileReader csvCountryFileReader;

    @BeforeEach
    public void setUp() {
        csvCountryFileReader = new CsvCountryFileReader();
    }

    @Test
    public void testReadCountryData_InvalidHeader() {
        String filePath = "de/bcxp/challenge/invalid_header_countries.csv";

        Executable executable = () -> csvCountryFileReader.readData(filePath, ";");

        FileFormatException exception = assertThrows(FileFormatException.class, executable);
        assertEquals("Invalid CSV file format. Expected headers: Name,Population,Area (km²)", exception.getMessage());
    }

    @Test
    public void testReadCountryData_InvalidLine() {
        String filePath = "de/bcxp/challenge/invalid_line_countries.csv";

        List<CountryRecord> countryRecords = csvCountryFileReader.readData(filePath, ";");

        // Check that valid records are read correctly
        assertEquals(1, countryRecords.size());
        assertEquals("Germany", countryRecords.get(0).getName());
        assertEquals(83120520, countryRecords.get(0).getPopulation(), 0.01);
        assertEquals(357386, countryRecords.get(0).getArea(), 0.01);

    }

    @Test
    public void testReadCountryData_ValidFile() {
        String filePath = "de/bcxp/challenge/test_countries.csv";

        List<CountryRecord> countryRecords = csvCountryFileReader.readData(filePath, ";");

        // Assertions
        assertEquals(3, countryRecords.size()); // Adjust based on your test file content
        assertEquals("Austria", countryRecords.get(0).getName());
        assertEquals(8926000, countryRecords.get(0).getPopulation(), 0.01);
        assertEquals(83855, countryRecords.get(0).getArea(), 0.01);
    }
}