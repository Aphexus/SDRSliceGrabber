package net.nicholaspurdy.gtrslicegrabber.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import net.nicholaspurdy.gtrslicegrabber.model.SliceFileItem;

import java.util.List;

class SliceGrabberStepListenerSupport extends StepListenerSupport<SliceFileItem,SliceFileItem> {

    private static final Logger log = LoggerFactory.getLogger(SliceGrabberStepListenerSupport.class);

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
                " finished with status: " + stepExecution.getStatus() +
                ". Rows inserted: " + stepExecution.getWriteCount());

        return stepExecution.getExitStatus();
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.debug("Committed " + context.getStepContext().getStepExecution().getWriteCount() + " rows for " +
                context.getStepContext().getJobParameters().get("assetClassParam") + " " +
                context.getStepContext().getJobParameters().get("dateStrParam"));
    }

    @Override
    public void afterRead(SliceFileItem item) {
        log.trace("Read item: {}", item.toString());
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("Exception occurred while trying to read CSV file.", ex);
    }

    @Override
    public void onProcessError(SliceFileItem item, Exception e) {
        log.error("Exception occurred while trying to process item: " + item, e);
    }

    @Override
    public void onWriteError(Exception ex, List<? extends SliceFileItem> items) {
        log.error("Exception occurred while trying to write to DB.", ex);
    }

}
