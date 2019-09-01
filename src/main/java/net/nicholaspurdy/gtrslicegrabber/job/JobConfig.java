package net.nicholaspurdy.gtrslicegrabber.job;

import net.nicholaspurdy.gtrslicegrabber.tasks.CsvFileCleanUpTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import net.nicholaspurdy.gtrslicegrabber.model.SliceFileItem;
import net.nicholaspurdy.gtrslicegrabber.tasks.SliceGrabberTasklet;
import net.nicholaspurdy.gtrslicegrabber.tasks.TaskletExceptionHandler;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class JobConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;

    @Autowired
    public JobConfig(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @JobScope
    @Bean
    public FlatFileItemReader<SliceFileItem> getReader(@Value("#{jobExecutionContext['file']}") File file) {

        FlatFileItemReader<SliceFileItem> itemReader = new FlatFileItemReaderBuilder<SliceFileItem>()
                .name("sliceFileItemReader")
                .resource(new FileSystemResource(file))
                .delimited()
                .names(JobUtils.FIELD_NAMES)
                .fieldSetMapper(new CustomBeanWrapperFieldSetMapper<>() {{
                    setTargetType(SliceFileItem.class);
                }}).build();

        itemReader.setLinesToSkip(1);

        return itemReader;
    }

    @JobScope
    @Bean
    public JdbcBatchItemWriter<SliceFileItem> getWriter(
            @Autowired DataSource dataSource,
            @Value("#{jobExecutionContext['fileId']}") Long fileId,
            @Value("#{jobParameters['assetClassParam']}") String assetClass) {

        return new JdbcBatchItemWriterBuilder<SliceFileItem>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(JobUtils.INSERT_STATEMENT
                        .replace("?", Long.toString(fileId)).replace("$TABLE", assetClass))
                .dataSource(dataSource)
                .build();
    }

    @Bean
    @Qualifier("step1")
    public Step getFileSendToS3ThenExtract(@Autowired SliceGrabberTasklet tasklet) {

        return this.stepBuilderFactory.get("getFileSendToS3ThenExtract")
                .tasklet(tasklet)
                .listener(new BasicStepExecutionListener())
                .exceptionHandler(new TaskletExceptionHandler())
                .build();
    }

    @Bean
    @Qualifier("step2")
    public Step readFileAndInsert(
            @Autowired FlatFileItemReader<SliceFileItem> itemReader,
            @Autowired ActionFieldItemProcessor itemProcessor,
            @Autowired JdbcBatchItemWriter<SliceFileItem> itemWriter) {

        return this.stepBuilderFactory.get("readFileAndInsert")
                .<SliceFileItem,SliceFileItem>chunk(2000)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .listener((StepExecutionListener) new SliceGrabberStepListenerSupport())
                .build();
    }

    @Bean
    @Qualifier("step3")
    public Step deleteCsvFile() {
        return this.stepBuilderFactory.get("deleteCsvFile")
                .tasklet(new CsvFileCleanUpTasklet())
                .listener(new BasicStepExecutionListener())
                .exceptionHandler(new TaskletExceptionHandler())
                .build();
    }

    @Bean
    public Job getJob(
            @Qualifier("step1") Step step1,
            @Qualifier("step2") Step step2,
            @Qualifier("step3") Step step3) {

        return this.jobBuilderFactory.get("sliceFileGrabberJob")
                .start(step1)
                .next(step2)
                .next(step3)
                .listener(new SliceGrabberJobListener())
                .build();
    }

}
