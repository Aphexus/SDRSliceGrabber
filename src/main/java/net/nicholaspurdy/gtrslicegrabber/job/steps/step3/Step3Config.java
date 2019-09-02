package net.nicholaspurdy.gtrslicegrabber.job.steps.step3;

import net.nicholaspurdy.gtrslicegrabber.job.steps.listeners.BasicStepExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Step3Config {

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public Step3Config(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @Qualifier("step3")
    public Step deleteCsvFile() {
        return this.stepBuilderFactory.get("deleteCsvFile")
                .tasklet(new CsvFileCleanUpTasklet())
                .listener(new BasicStepExecutionListener())
                .build();
    }

}
