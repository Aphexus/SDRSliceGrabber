package net.nicholaspurdy.gtrslicegrabber.exec;

import net.nicholaspurdy.gtrslicegrabber.utils.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public final class SliceGrabberCommandLineRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SliceGrabberCommandLineRunner.class);

    private final JobProcessManager manager;

    @Autowired
    public SliceGrabberCommandLineRunner(JobProcessManager manager) {
        this.manager = manager;
    }

    /**
     * This method does 4 things:
     *
     * 1. Validates the command line arguments
     * 2. Converts the arguments to POJOs
     * 3. Converts the POJOs to Spring Batch Jobs
     * 4. Launches said Jobs
     *
     */
    @Override
    public void run(final String... args) throws Exception {

        log.info("Executing command: " + String.join(" ", args));

        if (!RunnerUtils.validateArgs(args)) throw new IllegalArgumentException("Args are invalid.");

        List<JobParameters> jobParameters = RunnerUtils.generateParams(args);

        CountDownLatch latch = new CountDownLatch(jobParameters.size());

        manager.execute(jobParameters, latch);

        log.info("Main thread is beginning wait sequence.");
        Instant instant = Instant.now();

        latch.await();

        log.info("Main thread is done waiting. Entire program took " + FormatUtils.getTimeStr(instant, Instant.now()));

    }


}


