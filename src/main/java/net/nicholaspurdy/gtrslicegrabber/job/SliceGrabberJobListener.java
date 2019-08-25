package net.nicholaspurdy.gtrslicegrabber.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class SliceGrabberJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(SliceGrabberJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting timer for: " + jobExecution.getJobParameters().getString("assetClassParam")
                + " " + jobExecution.getJobParameters().getString("dateStrParam"));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job for: " + jobExecution.getJobParameters().getString("assetClassParam")
                + " " + jobExecution.getJobParameters().getString("dateStrParam") + " finished with status: " +
                jobExecution.getStatus() + ". Duration: " + getTimeStr(jobExecution));

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("Run these queries to clean up the database before retrying this job: " +
                    "DELETE FROM ENTRIES WHERE FILE_ID = " + jobExecution.getExecutionContext().get("fileId") +
                    "; DELETE FROM FILES WHERE FILE_ID = " + jobExecution.getExecutionContext().get("fileId") + ";");
        }
    }

    private String getTimeStr(JobExecution jobExecution) {

        long duration = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime() / 1000;

        if (duration > 59) {
            double d = duration / 60;
            return d + " MINUTES.";
        }
        else {
            return duration + " SECONDS.";
        }

    }

}
