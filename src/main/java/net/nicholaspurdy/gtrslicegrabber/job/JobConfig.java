package net.nicholaspurdy.gtrslicegrabber.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    @Autowired
    public JobConfig(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public Job getJob(
            @Qualifier("step1") Step step1,
            @Qualifier("step2") Step step2,
            @Qualifier("step3") Step step3,
            @Qualifier("step4") Step step4) {

        return this.jobBuilderFactory.get("sliceFileGrabberJob")
                .start(step1)
                .next(step2)
                .next(step3)
                .next(step4)
                .listener(new SliceGrabberJobListener())
                .build();
    }

}
