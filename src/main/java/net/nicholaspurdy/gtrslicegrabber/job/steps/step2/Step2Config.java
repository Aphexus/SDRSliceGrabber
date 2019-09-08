package net.nicholaspurdy.gtrslicegrabber.job.steps.step2;

import net.nicholaspurdy.gtrslicegrabber.job.steps.listeners.SliceGrabberStepListenerSupport;
import net.nicholaspurdy.gtrslicegrabber.model.SliceFileItem;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Step2Config {

    private static final ItemProcessor<SliceFileItem,SliceFileItem> sanityCheck = new SanityCheckItemProcessor();
    private static final StepExecutionListener listener = new SliceGrabberStepListenerSupport();

    private final StepBuilderFactory stepBuilderFactory;
    private final Integer chunkSize;

    @Autowired
    public Step2Config(StepBuilderFactory stepBuilderFactory,
                       @Value("${slicegrabber.itemwriter.chunkSize}") Integer chunkSize) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.chunkSize = chunkSize;
    }

    @Bean
    @Qualifier("lambdaStep2")
    public Step readFileAndInsertLambda(
            @Autowired FlatFileItemReader<SliceFileItem> itemReader,
            @Autowired CancelOrCorrectItemProcessor itemProcessor,
            @Autowired JdbcBatchItemWriter<SliceFileItem> itemWriter) {

        return this.stepBuilderFactory.get("readFileAndInsertLambda")
                .<SliceFileItem,SliceFileItem>chunk(chunkSize)
                .reader(itemReader)
                .processor(sanityCheck)
                .processor(itemProcessor)
                .writer(itemWriter)
                .listener(listener)
                .build();
    }

    @Bean
    @Qualifier("batchStep2")
    public Step readFileAndInsertBatch(
            @Autowired FlatFileItemReader<SliceFileItem> itemReader,
            @Autowired JdbcBatchItemWriter<SliceFileItem> itemWriter) {

        return this.stepBuilderFactory.get("readFileAndInsertBatch")
                .<SliceFileItem,SliceFileItem>chunk(chunkSize)
                .reader(itemReader)
                .processor(sanityCheck)
                .writer(itemWriter)
                .listener(listener)
                .build();
    }

}
