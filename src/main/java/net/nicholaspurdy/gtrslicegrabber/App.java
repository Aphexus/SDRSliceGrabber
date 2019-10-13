package net.nicholaspurdy.gtrslicegrabber;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import net.nicholaspurdy.gtrslicegrabber.model.MockContext;
import net.nicholaspurdy.gtrslicegrabber.model.AssetClass;
import net.nicholaspurdy.gtrslicegrabber.utils.DateUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the 2 entry points for this program: main (for AWS Batch), and handleRequest (for AWS Lambda).
 */
@SpringBootApplication
@EnableBatchProcessing
public class App implements RequestHandler<ScheduledEvent, String> {

    /**
     * This method is called when executing the program from AWS Batch.
     *
     * Example args:
     *
     * LAMBDA CREDITS 2019_01_08 2019_01_08
     *
     * BATCH RATES 2019_01_01 2019_04_01 FOREX 2019_01_01 2019_04_01 CREDITS 2019_01_01 2019_04_01 EQUITIES 2019_01_01 2019_04_01 COMMOTITIES 2019_01_01 2019_04_01
     *
     *
     * @param args The command line args specified in the JSON
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    /**
     * This method is called if the program is executed from AWS Lambda.
     *
     * @param event
     * See below JSON for example format.
     * {
     *   "id": "Manual Run",
     *   "detail-type": "Scheduled Event",
     *   "source": "aws.events",
     *   "account": "{{account-id}}",
     *   "time": "1970-01-01T00:00:00Z",
     *   "region": "us-east-1",
     *   "resources": [
     *     "{{ExampleRule}}"
     *   ],
     *   "detail": {
     *     "assetClasses": ["FOREX","RATES","CREDITS"]
     *   }
     * }
     *
     * If this lambda is triggered by CloudWatch, detail will be empty, and all asset classes should be grabbed.
     * The id field should be populated by the user if the lambda is being ran manually.
     *
     * "Command line" args format: "lambda" assetClass assetClass date
     *
     * @param context Contains AWS Request ID
     *
     * @return A string to mark the completion of the lambda.
     */
    @SuppressWarnings("unchecked")
    public String handleRequest(ScheduledEvent event, Context context) {

        StringBuilder cmd = new StringBuilder();

        cmd.append("LAMBDA ");

        List<AssetClass> assetClasses = (List<AssetClass>) event.getDetail()
                .getOrDefault("assetClasses", Arrays.asList(AssetClass.values()));

        for(AssetClass assetClass : assetClasses) {
            cmd.append(assetClass);
            cmd.append(" ");
            cmd.append(DateUtils.SDF.format(event.getTime().toDate()));
            cmd.append(" ");
            cmd.append(DateUtils.SDF.format(event.getTime().toDate()));
            cmd.append(" ");
        }


        SpringApplication.run(App.class, cmd.toString().split(" "));

        return "Event ID: " + event.getId() + " | Request ID: " + context.getAwsRequestId() + " END.";
    }

}

