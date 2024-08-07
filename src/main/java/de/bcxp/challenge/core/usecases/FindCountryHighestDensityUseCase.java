package de.bcxp.challenge.core.usecases;

import de.bcxp.challenge.core.entities.CountryRecord;
import de.bcxp.challenge.ports.ICountryRepository;

import java.util.List;

public class FindCountryHighestDensityUseCase {

    private final ICountryRepository countryRepository;

    public FindCountryHighestDensityUseCase(ICountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public CountryRecord execute() {
        List<CountryRecord> countryRecords = countryRepository.getAllCountryData();
        CountryRecord countryWithHighestDensity = null;

        for (CountryRecord record : countryRecords) {
            if (countryWithHighestDensity == null || record.getDensity() > countryWithHighestDensity.getDensity()) {
                countryWithHighestDensity = record;
            }
        }
        return countryWithHighestDensity;
    }
}
