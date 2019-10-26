package net.nicholaspurdy.gtrslicegrabber.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobProcessManager {

    private static final Logger log = LoggerFactory.getLogger(JobProcessManager.class);

    private final ApplicationContext context;
    private final ThreadPoolTaskExecutor executor;
    private final JobLauncher jobLauncher;

    @Autowired
    public JobProcessManager(ApplicationContext context, JobRepository jobRepository,
                             @Value("${slicegrabber.executor.threadPoolSize}") Integer threadPoolSize) {
        this.context = context;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setMaxPoolSize(threadPoolSize);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(Integer.MAX_VALUE);
        executor.initialize();
        this.executor = executor;

        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(executor);

        this.jobLauncher = jobLauncher;
    }

    public ThreadPoolTaskExecutor execute(List<JobParameters> jobParameters) {

        for (JobParameters params : jobParameters) {

            try {
                log.info("Building and launching job for: " + params.getString("assetClassParam") + " "
                        + params.getString("dateStrParam"));

                Job job = BeanFactoryAnnotationUtils.qualifiedBeanOfType(context.getAutowireCapableBeanFactory(),
                        Job.class, params.getString("jobTypeParam").toLowerCase());

                jobLauncher.run(job, params);
            }
            catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException e) {
                log.error("You accidentally tried to run the same job parameters twice.", e);
            }
            catch (JobRestartException e) {
                log.error("Job cannot be restarted.", e);
            }
            catch (JobParametersInvalidException e) {
                log.error("Exception due to improper job parameters.", e);
            }
            catch (Exception e) {
                log.error("Unspecified exception occurred for job: "
                        + params.getString("assetClassParam") + " " + params.getString("dateStrParam"), e);
            }
        }

        return executor;

    }

}
