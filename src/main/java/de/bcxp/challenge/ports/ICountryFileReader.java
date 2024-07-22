package de.bcxp.challenge.ports;

import de.bcxp.challenge.core.entities.CountryRecord;

import java.util.List;

public interface ICountryFileReader {
    List<CountryRecord> readCountryData(String filePath);
}
