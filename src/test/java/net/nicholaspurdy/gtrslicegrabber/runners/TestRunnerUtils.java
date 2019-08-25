package net.nicholaspurdy.gtrslicegrabber.runners;

import org.junit.Test;
import org.springframework.batch.core.JobParameters;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRunnerUtils {

    @Test
    public void testValidateArgs() {
        String validArgs[] = {"batch", "FOREX", "2016_02_02", "2016_02_04"};
        String invalidArgs[] = {"lambda FOREX 2016_04_05"};

        RunnerUtils.validateArgs(validArgs); // nothing should happen

        try {
            RunnerUtils.validateArgs(invalidArgs);
        }
        catch (IllegalArgumentException e) {
            assertTrue("Args are invalid.".equals(e.getMessage()));
        }
    }

    /**
     * The following two tests test the same method, 'generateParams(String... args)', but with different parameters.
     */
    @Test
    public void testLambdaGenerateParams() {

        // testing single asset class args
        String singleAssetClass[] = {"lambda", "RATES", "2016_04_05"};

        List<JobParameters> jobParameters = RunnerUtils.generateParams(singleAssetClass);

        assertEquals(1, jobParameters.size());
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));


        // testing multi asset class args
        String multiAssetClass[] = {"lambda", "FOREX", "RATES", "2016_04_05"};

        jobParameters = RunnerUtils.generateParams(multiAssetClass);

        assertEquals(2, jobParameters.size());
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("FOREX".equals(jobParameters.get(0).getString("assetClassParam")));

        assertTrue("2016_04_05".equals(jobParameters.get(1).getString("dateStrParam")));
        assertTrue("RATES".equals(jobParameters.get(1).getString("assetClassParam")));

    }

    @Test
    public void testBatchGenerateParams() {

        List<JobParameters> jobParameters;

        String singleAssetClassSingleDate[] = {"batch", "RATES", "2016_04_05", "2016_04_05"};
        String multiAssetClassSingleDates[] = {"batch", "RATES", "2016_04_05", "2016_04_05",
                "EQUITIES", "2016_04_05", "2016_04_05"};

        String singleAssetClassRangeDate[] = {"batch", "RATES", "2016_04_05", "2016_04_07"};
        String multiAssetClassRangeDate[] = {"batch", "RATES", "2016_04_05", "2016_04_06",
                "EQUITIES", "2016_04_05", "2016_04_07"};

        jobParameters = RunnerUtils.generateParams(singleAssetClassSingleDate);
        assertEquals(1, jobParameters.size());
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));


        jobParameters = RunnerUtils.generateParams(multiAssetClassSingleDates);
        assertEquals(2, jobParameters.size());
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));
        assertTrue("2016_04_05".equals(jobParameters.get(1).getString("dateStrParam")));
        assertTrue("EQUITIES".equals(jobParameters.get(1).getString("assetClassParam")));


        jobParameters = RunnerUtils.generateParams(singleAssetClassRangeDate);
        assertEquals(3, jobParameters.size());
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("2016_04_06".equals(jobParameters.get(1).getString("dateStrParam")));
        assertTrue("2016_04_07".equals(jobParameters.get(2).getString("dateStrParam")));


        jobParameters = RunnerUtils.generateParams(multiAssetClassRangeDate);
        assertEquals(5, jobParameters.size());
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("RATES".equals(jobParameters.get(1).getString("assetClassParam")));
        assertTrue("2016_04_06".equals(jobParameters.get(1).getString("dateStrParam")));

        assertTrue("EQUITIES".equals(jobParameters.get(2).getString("assetClassParam")));
        assertTrue("2016_04_05".equals(jobParameters.get(2).getString("dateStrParam")));
        assertTrue("2016_04_06".equals(jobParameters.get(3).getString("dateStrParam")));
        assertTrue("2016_04_07".equals(jobParameters.get(4).getString("dateStrParam")));
    }



}
