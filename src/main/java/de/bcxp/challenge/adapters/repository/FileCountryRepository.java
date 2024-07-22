package de.bcxp.challenge.adapters.repository;

import de.bcxp.challenge.core.entities.CountryRecord;
import de.bcxp.challenge.ports.ICountryFileReader;
import de.bcxp.challenge.ports.ICountryRepository;

import java.util.List;

public class FileCountryRepository implements ICountryRepository {
    private final ICountryFileReader countryFileReader;
    private final String filePath;

    public FileCountryRepository(ICountryFileReader countryFileReader, String filePath) {
        this.countryFileReader = countryFileReader;
        this.filePath = filePath;
    }

    @Override
    public List<CountryRecord> getAllCountryData() {
        return countryFileReader.readCountryData(filePath);
    }
}
