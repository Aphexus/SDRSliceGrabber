package net.nicholaspurdy.gtrslicegrabber.job.steps.step4;

import net.nicholaspurdy.gtrslicegrabber.job.steps.shared.CancelsAndCorrectsContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProcessCancelsAndCorrectsTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(ProcessCancelsAndCorrectsTasklet.class);

    private final CancelsAndCorrectsContainer container;
    private final CorrectsAndCancelsDao dao;

    @Autowired
    public ProcessCancelsAndCorrectsTasklet(CancelsAndCorrectsContainer container, CorrectsAndCancelsDao dao) {
        this.container = container;
        this.dao = dao;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Map<Long,Long> cancellations = container.getCancellations();
        Map<Long,Long> corrections = container.getCorrections();

        JobParameters params = chunkContext.getStepContext().getStepExecution().getJobParameters();
        String assetClass = params.getString("assetClassParam");

        int updated = 0;
        for (Map.Entry<Long,Long> entry : cancellations.entrySet()) {
            updated += dao.processCancel(entry, assetClass);
        }

        if (updated != cancellations.entrySet().size())
            log.warn("Only " + updated + " / " + cancellations.entrySet().size() + " CANCEL messages processed for " +
                    assetClass + " " + params.getString("dateStrParam"));

        updated = 0;
        for (Map.Entry<Long,Long> entry : corrections.entrySet()) {
            updated += dao.processCorrect(entry, assetClass);
        }

        if (updated != corrections.entrySet().size())
            log.warn("Only " + updated + " / " + corrections.entrySet().size() + " CORRECT messages processed for " +
                    assetClass + " " + params.getString("dateStrParam"));

        return RepeatStatus.FINISHED;
    }

}
