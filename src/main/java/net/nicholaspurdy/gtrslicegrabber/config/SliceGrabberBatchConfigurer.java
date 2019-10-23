package net.nicholaspurdy.gtrslicegrabber.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class SliceGrabberBatchConfigurer extends DefaultBatchConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SliceGrabberBatchConfigurer.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;


    @Override
    public JobRepository getJobRepository() {

        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_READ_UNCOMMITTED");
        factory.setValidateTransactionState(false);

        try {
            log.info("$$$$ LOOK HERE Constructing job repository in custom batch configurer." + (dataSource == null));
            return factory.getObject();
        } catch (Exception e) {
            log.error("Could not construct JobRepository bean.", e);
            throw new RuntimeException(e);
        }

    }




}
