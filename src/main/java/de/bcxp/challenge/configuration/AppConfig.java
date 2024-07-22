package de.bcxp.challenge.configuration;

import de.bcxp.challenge.adapters.repository.CsvCountryFileReader;
import de.bcxp.challenge.adapters.repository.CsvWeatherFileReader;
import de.bcxp.challenge.adapters.repository.FileCountryRepository;
import de.bcxp.challenge.adapters.repository.FileWeatherRepository;
import de.bcxp.challenge.adapters.service.ConsoleCountryDisplayService;
import de.bcxp.challenge.adapters.service.ConsoleWeatherDisplayService;
import de.bcxp.challenge.core.entities.CountryRecord;
import de.bcxp.challenge.core.entities.WeatherRecord;
import de.bcxp.challenge.core.usecases.FindCountryHighestDensityUseCase;
import de.bcxp.challenge.core.usecases.FindDayLowestTemperatureRangeUseCase;
import de.bcxp.challenge.core.usecases.GetCountryDataUseCase;
import de.bcxp.challenge.ports.*;

public class AppConfig {
    public static void main(String[] args) {

        // Creates File Readers and injects them on Repositories objects
        IFileReader<WeatherRecord> weatherFileReader = new CsvWeatherFileReader();
        IWeatherRepository weatherRepository = new FileWeatherRepository(weatherFileReader, "de/bcxp/challenge/weather.csv");

        IFileReader<CountryRecord> countryFileReader = new CsvCountryFileReader();
        ICountryRepository countryRepository = new FileCountryRepository(countryFileReader,"de/bcxp/challenge/countries.csv");

        // Services
        IWeatherDisplayService weatherDisplayService = new ConsoleWeatherDisplayService();
        ICountryStatisticsService countryStatisticsService = new ConsoleCountryDisplayService();

        // Use cases
        FindDayLowestTemperatureRangeUseCase findDayLowestTemperatureRangeUseCase = new FindDayLowestTemperatureRangeUseCase(weatherRepository);

        // Execute the required use cases and display data
        WeatherRecord dayWithLowestTemperatureRange = findDayLowestTemperatureRangeUseCase.execute();
        weatherDisplayService.displayDayWithLowestTemperatureRange(dayWithLowestTemperatureRange);

        // Country data configuration
        GetCountryDataUseCase getCountryDataUseCase = new GetCountryDataUseCase(countryRepository);
        FindCountryHighestDensityUseCase findCountryHighestDensityUseCase = new FindCountryHighestDensityUseCase();
        countryStatisticsService.displayCountryWithHighestDensity(findCountryHighestDensityUseCase.execute(getCountryDataUseCase.execute()));
    }
}