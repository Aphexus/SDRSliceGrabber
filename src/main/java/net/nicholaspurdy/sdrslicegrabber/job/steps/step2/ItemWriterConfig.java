package net.nicholaspurdy.sdrslicegrabber.job.steps.step2;

import net.nicholaspurdy.sdrslicegrabber.model.SliceFileItem;
import net.nicholaspurdy.sdrslicegrabber.utils.JobUtils;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ItemWriterConfig {

    @JobScope
    @Bean
    public JdbcBatchItemWriter<SliceFileItem> getWriter(
            @Autowired DataSource dataSource,
            @Value("#{jobExecutionContext['fileId']}") Long fileId,
            @Value("#{jobParameters['assetClassParam']}") String assetClass) {

        return new JdbcBatchItemWriterBuilder<SliceFileItem>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(JobUtils.getInsertStatement(assetClass, fileId))
                .dataSource(dataSource)
                .build();
    }

}
