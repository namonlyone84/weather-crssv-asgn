package com.crossover.trial.weather.client;

import com.crossover.trial.weather.entities.Airport;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AirportCSVParser {
    private static final String DEFAULT_CSV_FILE = "airports.dat";

    public List<Airport> readAirportFromFile(String filePath) {
        List<Airport> airports = new ArrayList<>();
        filePath = getPathOrDefault(filePath);

        try (

                FileReader airportFileReader = new FileReader(filePath);
                ICsvBeanReader csvReader = new CsvBeanReader(airportFileReader, CsvPreference.STANDARD_PREFERENCE)
        ) {
            // the header elements are used to map the values to the bean
            final String[] headers = getHeaders();
            final CellProcessor[] fieldProcessors = getFieldProcessors();

            Airport airport;
            while ((airport = csvReader.read(Airport.class, headers, fieldProcessors)) != null) {
                airports.add(airport);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return airports;
    }

    private String[] getHeaders() {
        return new String[]{null, null, null, null, "iata", null, "latitude", "longitude",
                null, null, null};
    }

    private String getPathOrDefault(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            filePath = this.getClass().getClassLoader().getResource(DEFAULT_CSV_FILE).getPath();
        }
        return filePath;
    }

    private CellProcessor[] getFieldProcessors() {
        final CellProcessor[] processors = new CellProcessor[] {
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
                new NotNull(), // iata
                new Optional(),
                new NotNull(new ParseDouble()), // latitude
                new NotNull(new ParseDouble()), // longtitude
                new Optional(),
                new Optional(),
                new Optional(),
        };
        return processors;
    }
}
