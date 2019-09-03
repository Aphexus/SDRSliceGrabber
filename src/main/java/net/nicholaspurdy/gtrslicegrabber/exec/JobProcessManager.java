package net.nicholaspurdy.gtrslicegrabber.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import net.nicholaspurdy.gtrslicegrabber.model.AssetClass;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class JobProcessManager {

    private static final Logger log = LoggerFactory.getLogger(JobProcessManager.class);

    private final ApplicationContext context;
    private final JobLauncher jobLauncher;
    private final Integer threadPoolSize;

    private ExecutorService ratesExecutor;
    private ExecutorService equitiesExecutor;
    private ExecutorService commoditiesExecutor;
    private ExecutorService forexExecutor;
    private ExecutorService creditsExecutor;

    @Autowired
    public JobProcessManager(ApplicationContext context, JobLauncher jobLauncher,
                             @Value("${slicegrabber.executors.threadPoolSize}") Integer threadPoolSize) {
        this.context = context;
        this.jobLauncher = jobLauncher;
        this.threadPoolSize = threadPoolSize;
    }

    public void execute(List<JobParameters> jobParameters, CountDownLatch latch) {

        for (JobParameters params : jobParameters) {

            Runnable runnable = generateRunnable(params, latch);

            AssetClass assetClass = AssetClass.valueOf(params.getString("assetClassParam"));

            Executor executor = getExecutor(assetClass);

            executor.execute(runnable);
        }

        shutdownExecutors();

    }

    private Runnable generateRunnable(JobParameters params, CountDownLatch latch) {

        return () -> {

            try {
                log.info("Building & launching job for: " + params.getString("assetClassParam")
                        + " " + params.getString("dateStrParam"));
                Job job = context.getBean(Job.class);
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
            finally {
                latch.countDown();
            }

        };

    }

    private Executor getExecutor(AssetClass assetClass) {

        switch (assetClass) {

            case FOREX:
                forexExecutor = forexExecutor == null ? Executors.newFixedThreadPool(threadPoolSize) : forexExecutor;
                return forexExecutor;

            case RATES:
                ratesExecutor = ratesExecutor == null ? Executors.newFixedThreadPool(threadPoolSize) : ratesExecutor;
                return ratesExecutor;

            case CREDITS:
                creditsExecutor = creditsExecutor == null ? Executors.newFixedThreadPool(threadPoolSize) : creditsExecutor;
                return creditsExecutor;

            case EQUITIES:
                equitiesExecutor = equitiesExecutor == null ? Executors.newFixedThreadPool(threadPoolSize) : equitiesExecutor;
                return equitiesExecutor;

            default:
                commoditiesExecutor = commoditiesExecutor == null ? Executors.newFixedThreadPool(threadPoolSize) : commoditiesExecutor;
                return commoditiesExecutor;

        }

    }

    private void shutdownExecutors() {

        if(ratesExecutor != null) ratesExecutor.shutdown();

        if(commoditiesExecutor != null) commoditiesExecutor.shutdown();

        if(creditsExecutor != null) creditsExecutor.shutdown();

        if(forexExecutor != null) forexExecutor.shutdown();

        if(equitiesExecutor != null) equitiesExecutor.shutdown();

    }


}
