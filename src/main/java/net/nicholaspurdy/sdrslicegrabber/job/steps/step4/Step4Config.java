package net.nicholaspurdy.sdrslicegrabber.job.steps.step4;

import net.nicholaspurdy.sdrslicegrabber.job.steps.listeners.BasicStepExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Only lambda jobs will use this step
 */
@Configuration
public class Step4Config {

    private static final StepExecutionListener listener = new BasicStepExecutionListener();

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public Step4Config(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @Qualifier("step4")
    public Step processCancelsAndCorrects(@Autowired ProcessCancelsAndCorrectsTasklet tasklet) {
        return this.stepBuilderFactory.get("processCancelsAndCorrects")
                .tasklet(tasklet)
                .listener(listener)
                .build();

    }
}
