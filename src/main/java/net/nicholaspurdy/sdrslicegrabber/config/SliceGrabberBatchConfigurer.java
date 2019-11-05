package net.nicholaspurdy.sdrslicegrabber.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class SliceGrabberBatchConfigurer extends DefaultBatchConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SliceGrabberBatchConfigurer.class);

    private DataSource dataSource;

    @Autowired
    public SliceGrabberBatchConfigurer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public JobRepository getJobRepository() {

        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(getTransactionManager());
        factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factory.setValidateTransactionState(false);

        try {
            return factory.getObject();
        } catch (Exception e) {
            log.error("Could not construct JobRepository bean.", e);
            throw new RuntimeException(e);
        }

    }

}
