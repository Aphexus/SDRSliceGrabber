package net.nicholaspurdy.gtrslicegrabber.runners;

//import net.nicholaspurdy.gtrslicegrabber.exec.RunnerUtils;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class TestRunnerUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
    private static final LocalDate today = LocalDate.now(Clock.systemUTC());


    @Test
    public void testValidateArgs() {
        final String[] nullCmd = null, emptyCmd = {},
                badAssetClass = { "COLLATERAL", "2013_02_23" },
                missingDate = { "EQUITIES", "2014_01_09" },
                badDateFormat = { "FOREX", "2014_04_20", "2014-05-09" },
                tomorrowsDate = { "RATES", "2015_05_06", dateFormat.format(Date.valueOf(today.plusDays(1))) },
                invalidRange = { "RATES", "2014-03-03", "2014-03-01" },
                incompleteStr = { "RATES", "2015_04_03", "2015_04_06", "EQUITIES", "2016_04_05" },
                extraArg = { "RATES", "2015_04_03", "2015_04_06", "FOREX", "2015_04_03", "2015_04_06",
                        "COMMODITIES", "2015_04_03", "2015_04_06", "EQUITIES", "2015_04_03", "2015_04_06",
                        "CREDITS", "2015_04_03", "2015_04_06", "EXTRA" },
                valid = { "RATES", "2015_04_03", "2015_04_06", "FOREX", "2015_04_03", "2015_04_06",
                        "COMMODITIES", "2015_04_03", "2015_04_06", "EQUITIES", "2015_04_03", "2015_04_06",
                        "CREDITS", "2015_04_03", "2015_04_06" };


//        assertFalse(RunnerUtils.validateArgs(nullCmd));
//        assertFalse(RunnerUtils.validateArgs(emptyCmd));
//        assertFalse(RunnerUtils.validateArgs(badAssetClass));
//        assertFalse(RunnerUtils.validateArgs(missingDate));
//        assertFalse(RunnerUtils.validateArgs(badDateFormat));
//        assertFalse(RunnerUtils.validateArgs(tomorrowsDate));
//        assertFalse(RunnerUtils.validateArgs(invalidRange));
//        assertFalse(RunnerUtils.validateArgs(incompleteStr));
//        assertFalse(RunnerUtils.validateArgs(extraArg));
//        assertTrue(RunnerUtils.validateArgs(valid));

    }

    @Test
    public void testGenerateParams() {

        List<JobParameters> jobParameters=null;

        String singleAssetClassSingleDate[] = {"RATES", "2016_04_05", "2016_04_05"};
        String multiAssetClassSingleDates[] = {"RATES", "2016_04_05", "2016_04_05",
                "EQUITIES", "2016_04_05", "2016_04_05"};

        String singleAssetClassRangeDate[] = {"RATES", "2016_04_05", "2016_04_07"};
        String multiAssetClassRangeDate[] = {"RATES", "2016_04_05", "2016_04_06",
                "EQUITIES", "2016_04_05", "2016_04_07"};

        //jobParameters = RunnerUtils.generateParams(singleAssetClassSingleDate);
        assertEquals(1, jobParameters.size());
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));


        //jobParameters = RunnerUtils.generateParams(multiAssetClassSingleDates);
        assertEquals(2, jobParameters.size());
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));
        assertTrue("2016_04_05".equals(jobParameters.get(1).getString("dateStrParam")));
        assertTrue("EQUITIES".equals(jobParameters.get(1).getString("assetClassParam")));


        //jobParameters = RunnerUtils.generateParams(singleAssetClassRangeDate);
        assertEquals(3, jobParameters.size());
        assertTrue("RATES".equals(jobParameters.get(0).getString("assetClassParam")));
        assertTrue("2016_04_05".equals(jobParameters.get(0).getString("dateStrParam")));
        assertTrue("2016_04_06".equals(jobParameters.get(1).getString("dateStrParam")));
        assertTrue("2016_04_07".equals(jobParameters.get(2).getString("dateStrParam")));


        //jobParameters = RunnerUtils.generateParams(multiAssetClassRangeDate);
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
