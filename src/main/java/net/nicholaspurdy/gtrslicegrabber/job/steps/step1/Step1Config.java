package net.nicholaspurdy.gtrslicegrabber.job.steps.step1;

import net.nicholaspurdy.gtrslicegrabber.job.steps.listeners.BasicStepExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Step1Config {

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public Step1Config(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @Qualifier("step1")
    public Step getFileSendToS3ThenExtract(@Autowired SliceGrabberTasklet tasklet) {

        return this.stepBuilderFactory.get("getFileSendToS3ThenExtract")
                .tasklet(tasklet)
                .listener(new BasicStepExecutionListener())
                .build();
    }

}
