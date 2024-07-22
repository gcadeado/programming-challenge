package de.bcxp.challenge.adapters.repository;

import de.bcxp.challenge.core.entities.CountryRecord;
import de.bcxp.challenge.exceptions.FileFormatException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class CsvCountryFileReader extends CsvFileReader<CountryRecord> {

    @Override
    protected CountryRecord parseLine(String[] values, Map<String, Integer> columnIndexes) throws FileFormatException {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

        if (values.length < columnIndexes.size()) {
            throw new FileFormatException("Invalid CSV line: " + String.join(",", values));
        }

        try {
            String countryName = values[columnIndexes.get("Name")];
            Long countryPopulation = format.parse(values[columnIndexes.get("Population")]).longValue();
            Double countryArea = format.parse(values[columnIndexes.get("Area (km²)")]).doubleValue();
            return new CountryRecord(countryName, countryPopulation, countryArea);
        } catch (NumberFormatException | ParseException e) {
            throw new FileFormatException("Invalid CSV line: " + String.join(",", values));
        }
    }

    @Override
    protected String[] getExpectedHeaders() {
        return new String[]{"Name", "Population", "Area (km²)"};
    }

}