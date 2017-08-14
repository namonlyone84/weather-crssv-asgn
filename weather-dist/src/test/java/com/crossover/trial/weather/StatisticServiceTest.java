package com.crossover.trial.weather;

import com.crossover.trial.weather.repository.FrequencyRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.crossover.trial.weather.service.StatisticService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;

public class StatisticServiceTest {
    private static final String[] IATA_CODES = new String[]{"BOS", "EWR", "LCY", "STN"};

    private static final Double[] RADIUS = new Double[]{5d, 15d, 20d, 5.5};

    private FrequencyRepository frequencyRepository = RepositoryFactory.getFrequencyRepository(CURRENT_REPOSITORY_TYPE);

    private StatisticService statisticService = new StatisticService();

    public StatisticServiceTest() throws IllegalAccessException, InstantiationException {
    }

    @Before
    public void setUp() {
        frequencyRepository.clear();
    }

    @Test
    public void testComputeRadiusFrequency_noData() {
        int[] range = statisticService.computeRadiusFrequencies();
        Assert.assertEquals(1, range.length);
        Assert.assertEquals(0, range[0]);
    }

    @Test
    public void testComputeRadiusFrequency_differentRadius() {
        prepareFrequencies();
        int[] range = statisticService.computeRadiusFrequencies();

        Assert.assertEquals(RADIUS[2].intValue() + 1, range.length);

        // Each radius has one request
        Assert.assertEquals(range[RADIUS[0].intValue()], 1);
        Assert.assertEquals(range[RADIUS[1].intValue()], 1);
        Assert.assertEquals(range[RADIUS[2].intValue()], 1);
    }

    @Test
    public void testComputeRadiusFrequency_coincidentRadius() {
        prepareFrequencies();
        frequencyRepository.update(IATA_CODES[0], RADIUS[3]);

        int[] range = statisticService.computeRadiusFrequencies();

        Assert.assertEquals(RADIUS[2].intValue() + 1, range.length);

        // RADIUS[0] and RADIUS[3] are the same by integer value
        Assert.assertEquals(range[RADIUS[0].intValue()], 2);
        Assert.assertEquals(range[RADIUS[1].intValue()], 1);
        Assert.assertEquals(range[RADIUS[2].intValue()], 1);
    }

    private void prepareFrequencies() {
        frequencyRepository.update(IATA_CODES[0], RADIUS[0]);
        frequencyRepository.update(IATA_CODES[1], RADIUS[1]);
        frequencyRepository.update(IATA_CODES[0], RADIUS[2]);
    }
}
