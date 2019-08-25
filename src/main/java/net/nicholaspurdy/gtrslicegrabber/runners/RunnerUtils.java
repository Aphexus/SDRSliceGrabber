package net.nicholaspurdy.gtrslicegrabber.runners;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import net.nicholaspurdy.gtrslicegrabber.utils.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Package private
 */
final class RunnerUtils {

    private RunnerUtils() { }

    /**
     * Validates args using CommandLineArgsValidator class.
     *
     * If the args are not valid, it will throw an exception.
     */
    static void validateArgs(String... args) {

        if(CommandLineArgsValidator.validateLambdaArgs(args) || CommandLineArgsValidator.validateBatchArgs(args)) {
            return;
        }

        throw new IllegalArgumentException("Args are invalid."); // need to see how Lambda or Batch will handle this

    }

    /**
     * Passes command line args to appropriate private method to generate the JobParameters.
     *
     * This method assumes that the args have already been validated.
     */
    static List<JobParameters> generateParams(String... args) {

        if("lambda".equals(args[0]))
            return generateLambdaParams(args);

        else // "batch"
            return generateBatchParams(args);

    }

    /**
     * Generates an instance of JobParameters per Asset Class in the command line args.
     *
     * This method assumes that the args have already been validated.
     */
    private static List<JobParameters> generateLambdaParams(String... args) {

        List<JobParameters> jobParametersList = new ArrayList<>();

        JobParameter dateStrParam = new JobParameter(args[args.length-1], true);
        JobParameter runDateParam = new JobParameter(new Date(), true);

        // first arg is 'lambda', so it is skipped, last arg is the date, hence it is also skipped
        // eg. "lambda FOREX CREDITS RATES 2017_02_20"
        for(String assetClass : Arrays.copyOfRange(args, 1, args.length-1)) {

            JobParameter assetClassParam = new JobParameter(assetClass, true);

            Map<String, JobParameter> map = new HashMap<>();
            map.put("dateStrParam", dateStrParam);
            map.put("runDateParam", runDateParam);
            map.put("assetClassParam", assetClassParam);

            JobParameters jobParameters = new JobParameters(map);

            jobParametersList.add(jobParameters);
        }

        return jobParametersList;
    }

    /**
     * Generates an instance of JobParameters per Asset Class, per day in the date range from the command line args.
     *
     * This method assumes that the args have already been validated.
     */
    private static List<JobParameters> generateBatchParams(String... args) {

        List<JobParameters> jobParametersList = new ArrayList<>();

        JobParameter runDateParam = new JobParameter(new Date(), true);

        // "batch", "RATES", "2015_04_03", "2015_04_06", "CREDITS", "2015_04_03", "2015_04_06"
        for(int i = 1; i <= args.length - 3; i += 3) {

            JobParameter assetClassParam = new JobParameter(args[i], true);

            LocalDate startDate = LocalDate.parse(args[i+1], DateUtils.DTF);
            LocalDate endDate = LocalDate.parse(args[i+2], DateUtils.DTF);

            for(LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

                JobParameter dateStrParam = new JobParameter(date.format(DateUtils.DTF), true);

                Map<String, JobParameter> map = new HashMap<>();
                map.put("dateStrParam", dateStrParam);
                map.put("runDateParam", runDateParam);
                map.put("assetClassParam", assetClassParam);

                JobParameters jobParameters = new JobParameters(map);

                jobParametersList.add(jobParameters);
            }

        }

        return jobParametersList;
    }

}
