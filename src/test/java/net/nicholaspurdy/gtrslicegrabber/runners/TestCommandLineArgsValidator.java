package net.nicholaspurdy.gtrslicegrabber.runners;

import org.junit.Test;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCommandLineArgsValidator {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
    private static final LocalDate today = LocalDate.now(Clock.systemUTC());

    @Test
    public void testValidateLambdaArgs() {

        String[] nullCmd = null, emptyCmd = {}, batchCmd = {"batch"},
                badAssetClass = {"lambda", "COLLATERAL", "2013_02_22"},
                missingDate = {"lambda", "RATES", "EQUITIES"},
                badDateFormat = {"lambda", "RATES", "2015-12-06"},
                tomorrowsDate = {"lambda", "RATES", dateFormat.format(Date.valueOf(today.plusDays(1)))},
                valid = {"lambda", "RATES", "FOREX", "2018_10_14"};


        assertFalse(CommandLineArgsValidator.validateLambdaArgs(nullCmd));
        assertFalse(CommandLineArgsValidator.validateLambdaArgs(emptyCmd));
        assertFalse(CommandLineArgsValidator.validateLambdaArgs(batchCmd));
        assertFalse(CommandLineArgsValidator.validateLambdaArgs(badAssetClass));
        assertFalse(CommandLineArgsValidator.validateLambdaArgs(missingDate));
        assertFalse(CommandLineArgsValidator.validateLambdaArgs(badDateFormat));
        assertFalse(CommandLineArgsValidator.validateLambdaArgs(tomorrowsDate));
        assertTrue(CommandLineArgsValidator.validateLambdaArgs(valid));
    }

    @Test
    public void testValidateBatchArgs() {
        final String[] nullCmd = null, emptyCmd = {}, lambdaCmd = { "lambda" },
                badAssetClass = { "batch", "COLLATERAL", "2013_02_23" },
                missingDate = { "batch", "EQUITIES", "2014_01_09" },
                badDateFormat = { "batch", "FOREX", "2014_04_20", "2014-05-09" },
                tomorrowsDate = { "batch", "RATES", "2015_05_06", dateFormat.format(Date.valueOf(today.plusDays(1))) },
                invalidRange = { "batch", "RATES", "2014-03-03", "2014-03-01" },
                incompleteStr = { "batch", "RATES", "2015_04_03", "2015_04_06", "EQUITIES", "2016_04_05" },
                extraArg = { "batch", "RATES", "2015_04_03", "2015_04_06", "FOREX", "2015_04_03", "2015_04_06",
                        "COMMODITIES", "2015_04_03", "2015_04_06", "EQUITIES", "2015_04_03", "2015_04_06",
                        "CREDITS", "2015_04_03", "2015_04_06", "EXTRA" },
                valid = { "batch", "RATES", "2015_04_03", "2015_04_06", "FOREX", "2015_04_03", "2015_04_06",
                        "COMMODITIES", "2015_04_03", "2015_04_06", "EQUITIES", "2015_04_03", "2015_04_06",
                        "CREDITS", "2015_04_03", "2015_04_06" };

        assertFalse(CommandLineArgsValidator.validateBatchArgs(nullCmd));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(emptyCmd));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(lambdaCmd));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(badAssetClass));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(missingDate));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(badDateFormat));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(tomorrowsDate));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(invalidRange));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(incompleteStr));
        assertFalse(CommandLineArgsValidator.validateBatchArgs(extraArg));
        assertTrue(CommandLineArgsValidator.validateBatchArgs(valid));

    }
}
