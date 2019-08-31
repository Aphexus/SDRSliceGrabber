package net.nicholaspurdy.gtrslicegrabber.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.File;

public class CsvFileCleanUpTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(CsvFileCleanUpTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        File csvFile = ((File) chunkContext.getStepContext().getJobExecutionContext().get("file"));

        if (!csvFile.delete()) {
            log.warn("Could not delete " + csvFile.getName());
        }

        return RepeatStatus.FINISHED;
    }
}
