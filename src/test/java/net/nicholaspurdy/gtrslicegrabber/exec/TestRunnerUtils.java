package net.nicholaspurdy.sdrslicegrabber.exec;

//import net.nicholaspurdy.sdrslicegrabber.exec.RunnerUtils;
import net.nicholaspurdy.sdrslicegrabber.exec.RunnerUtils;
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
                badAssetClass = { "LAMBDA", "COLLATERAL", "2013_02_23" },
                missingDate = { "LAMBDA", "EQUITIES", "2014_01_09" },
                badDateFormat = { "LAMBDA", "FOREX", "2014_04_20", "2014-05-09" },
                tomorrowsDate = { "LAMBDA", "RATES", "2015_05_06", dateFormat.format(Date.valueOf(today.plusDays(1))) },
                invalidRange = { "LAMBDA", "RATES", "2014-03-03", "2014-03-01" },
                incompleteStr = { "LAMBDA", "RATES", "2015_04_03", "2015_04_06", "EQUITIES", "2016_04_05" },
                extraArg = { "LAMBDA", "RATES", "2015_04_03", "2015_04_06", "FOREX", "2015_04_03", "2015_04_06",
                        "COMMODITIES", "2015_04_03", "2015_04_06", "EQUITIES", "2015_04_03", "2015_04_06",
                        "CREDITS", "2015_04_03", "2015_04_06", "EXTRA" },
                valid = { "LAMBDA", "RATES", "2015_04_03", "2015_04_06", "FOREX", "2015_04_03", "2015_04_06",
                        "COMMODITIES", "2015_04_03", "2015_04_06", "EQUITIES", "2015_04_03", "2015_04_06",
                        "CREDITS", "2015_04_03", "2015_04_06" };


        assertFalse(RunnerUtils.argsAreValid(nullCmd));
        assertFalse(RunnerUtils.argsAreValid(emptyCmd));
        assertFalse(RunnerUtils.argsAreValid(badAssetClass));
        assertFalse(RunnerUtils.argsAreValid(missingDate));
        assertFalse(RunnerUtils.argsAreValid(badDateFormat));
        assertFalse(RunnerUtils.argsAreValid(tomorrowsDate));
        assertFalse(RunnerUtils.argsAreValid(invalidRange));
        assertFalse(RunnerUtils.argsAreValid(incompleteStr));
        assertFalse(RunnerUtils.argsAreValid(extraArg));
        assertTrue(RunnerUtils.argsAreValid(valid));

    }

    @Test
    public void testGenerateParams() {

        List<JobParameters> jobParameters=null;

        String[] singleAssetClassSingleDate = {"LAMBDA", "RATES", "2016_04_05", "2016_04_05"};
        String[] multiAssetClassSingleDates = {"LAMBDA", "RATES", "2016_04_05", "2016_04_05",
                "EQUITIES", "2016_04_05", "2016_04_05"};

        String[] singleAssetClassRangeDate = {"LAMBDA", "RATES", "2016_04_05", "2016_04_07"};
        String[] multiAssetClassRangeDate = {"LAMBDA", "RATES", "2016_04_05", "2016_04_06",
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
