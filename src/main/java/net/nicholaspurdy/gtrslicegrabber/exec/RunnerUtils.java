package net.nicholaspurdy.gtrslicegrabber.exec;

import net.nicholaspurdy.gtrslicegrabber.model.AssetClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import net.nicholaspurdy.gtrslicegrabber.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Package private
 */
final class RunnerUtils {

    private static final Logger log = LoggerFactory.getLogger(RunnerUtils.class);

    private RunnerUtils() { }

    static boolean argsAreValid(String... args) {

        if (args == null || args.length == 0) {
            log.error("No args were passed in.");
            return false;
        }

        if (args.length > 16) {
            log.error("Too many args passed.");
            return false;
        }

        if (!"LAMBDA".equals(args[0]) && !"BATCH".equals(args[0])) {
            log.error("First argument must be LAMBDA or BATCH.");
            return false;
        }

        int i = 1;
        try {
            for( ; i < args.length; i += 3) {
                AssetClass.valueOf(args[i]);

                LocalDate startDate = LocalDate.parse(args[i+1], DateUtils.DTF);
                LocalDate endDate = LocalDate.parse(args[i+2], DateUtils.DTF);

                if(startDate.isAfter(DateUtils.TODAY) || endDate.isAfter(DateUtils.TODAY) || endDate.isBefore(startDate)) {
                    log.error("Invalid date range. Today: " + DateUtils.TODAY + ". Start: " + startDate + ". End: " + endDate);
                    return false;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            log.error("Incomplete asset class + date range combination.");
            return false;
        }
        catch (IllegalArgumentException e) {
            log.error("Invalid asset class detected: " + args[i]);
            return false;
        }
        catch (DateTimeParseException e) {
            log.error("Invalid date format. Date's must have yyyy_MM_dd format.");
            return false;
        }


        return true;
    }


    /**
     * Generates an instance of JobParameters per Asset Class, per day in the date range from the command line args.
     *
     * This method assumes that the args have already been validated.
     */
    static List<JobParameters> generateParams(String... args) {

        List<JobParameters> jobParametersList = new ArrayList<>();

        JobParameter runDateParam = new JobParameter(new Date(), true);
        JobParameter jobTypeParam = new JobParameter(args[0], true);

        // "RATES", "2015_04_03", "2015_04_06", "CREDITS", "2015_04_03", "2015_04_06"
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
                map.put("jobTypeParam", jobTypeParam);

                JobParameters jobParameters = new JobParameters(map);

                jobParametersList.add(jobParameters);
            }

        }

        return jobParametersList;
    }


}
