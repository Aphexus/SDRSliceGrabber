package net.nicholaspurdy.gtrslicegrabber.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class BasicStepExecutionListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(BasicStepExecutionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Starting step: " + stepExecution.getStepName() + " for " +
                stepExecution.getJobParameters().getString("assetClassParam") + " " +
                stepExecution.getJobParameters().getString("dateStrParam"));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Step " + stepExecution.getStepName() + " for " +
                stepExecution.getJobParameters().getString("assetClassParam") + " " +
                stepExecution.getJobParameters().getString("dateStrParam") +
                " finished with status: " + stepExecution.getStatus());

        return null;
    }

}
