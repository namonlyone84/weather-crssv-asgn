package com.crossover.trial.weather.client;

import com.crossover.trial.weather.entity.Airport;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AirportCSVParser {
    private static final String DEFAULT_CSV_FILE = "airports.dat";
    private static Logger logger = Logger.getLogger(AirportCSVParser.class.getName());

    public List<Airport> readAirportsFromFile(String filePath) {
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
            logger.log(Level.SEVERE, "Cannot file CSV file", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
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
        final CellProcessor[] processors = new CellProcessor[]{
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(),
                new Optional(), // iata
                new Optional(),
                new Optional(new ParseDouble()), // latitude
                new Optional(new ParseDouble()), // longtitude
                new Optional(),
                new Optional(),
                new Optional(),
        };
        return processors;
    }
}
