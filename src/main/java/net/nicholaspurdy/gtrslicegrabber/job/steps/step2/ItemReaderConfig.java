package net.nicholaspurdy.gtrslicegrabber.job.steps.step2;

import net.nicholaspurdy.gtrslicegrabber.model.SliceFileItem;
import net.nicholaspurdy.gtrslicegrabber.utils.JobUtils;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class ItemReaderConfig {

    @JobScope
    @Bean
    public FlatFileItemReader<SliceFileItem> getReader(@Value("#{jobExecutionContext['file']}") File file) {

        FlatFileItemReader<SliceFileItem> itemReader = new FlatFileItemReaderBuilder<SliceFileItem>()
                .name("sliceFileItemReader")
                .resource(new FileSystemResource(file))
                .delimited()
                .names(JobUtils.FIELD_NAMES)
                .fieldSetMapper(new CustomBeanWrapperFieldSetMapper<SliceFileItem>() {{
                    setTargetType(SliceFileItem.class);
                }}).build();

        itemReader.setLinesToSkip(1);

        return itemReader;
    }

}
