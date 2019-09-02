package net.nicholaspurdy.gtrslicegrabber.job.steps.step2;

import net.nicholaspurdy.gtrslicegrabber.job.steps.listeners.SliceGrabberStepListenerSupport;
import net.nicholaspurdy.gtrslicegrabber.model.SliceFileItem;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Step2Config {

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public Step2Config(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @Qualifier("step2")
    public Step readFileAndInsert(
            @Autowired FlatFileItemReader<SliceFileItem> itemReader,
            @Autowired JdbcBatchItemWriter<SliceFileItem> itemWriter) {

        return this.stepBuilderFactory.get("readFileAndInsert")
                .<SliceFileItem,SliceFileItem>chunk(2000)
                .reader(itemReader)
                .processor(new SanityCheckItemProcessor())
                .writer(itemWriter)
                .listener((StepExecutionListener) new SliceGrabberStepListenerSupport())
                .build();
    }

}
