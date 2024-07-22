package de.bcxp.challenge.core.entities;

public class CountryRecord {
    private final String name;
    private final Long population;
    private final Double area;

    public CountryRecord(String name, Long population, Double area) {
        this.name = name;
        this.population = population;
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public Number getPopulation() {
        return population;
    }

    public Number getArea() {
        return area;
    }

    public Double getDensity()
    {
        return (double)population / (double)area;
    }
}
