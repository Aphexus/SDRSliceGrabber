package net.nicholaspurdy.gtrslicegrabber.runners;

import net.nicholaspurdy.gtrslicegrabber.model.AssetClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import net.nicholaspurdy.gtrslicegrabber.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Package private
 */
final class CommandLineArgsValidator {

    private static final Logger log = LoggerFactory.getLogger(CommandLineArgsValidator.class);

    private CommandLineArgsValidator() { }

    /**
     * This method validates the "command line" arguments for an AWS Lambda function
     */
    static boolean validateLambdaArgs(@Nullable String... args) {

        if(args == null || args.length == 0) {
            log.error("No args were passed in.");
            return false;
        }

        if(!"lambda".equals(args[0])) {
            log.error("First arg is not \"lambda\". Actual first arg: \"" + args[0] + "\"");
            return false;
        }

        // validate asset class arg
        int i = 1;
        try {
            for( ; i < args.length - 1; i++)
                AssetClass.valueOf(args[i]);
        }
        catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            log.error("Middle arguments of lambda cmd must be a valid asset class string. Bad string: " + args[i]);
            return false;
        }

        // validate date arg
        try {
            LocalDate target = LocalDate.parse(args[args.length - 1], DateUtils.DTF);

            if(target.isAfter(DateUtils.TODAY)) {
                log.error("Target date cannot be greater than today's date (in UTC). Target date: "
                        + target + ". Today's date: " + DateUtils.TODAY);
                return false;
            }
        }
        catch(DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            log.error("Last argument of lambda cmd must be a date in yyyy_MM_dd format.");
            return false;
        }

        return true;
    }

    /**
     * This method validates the "command line" arguments for an AWS Batch job
     */
    static boolean validateBatchArgs(@Nullable String... args) {

        if(args == null || args.length == 0) {
            log.error("No args were passed in.");
            return false;
        }

        if(!"batch".equals(args[0])) {
            log.error("First arg is not \"batch\". Actual first arg: " + args[0]);
            return false;
        }

        if(args.length > 16) {
            log.error("Too many args for batch job.");
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
}
