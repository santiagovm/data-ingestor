package com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration;

import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.UsgsEarthquake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
public class ItemReaderConfig {

    @Bean
    @StepScope // allows late binding of the file parameter
    public FlatFileItemReader<UsgsEarthquake> itemReader(@Value("#{stepExecutionContext[filename]}") String filename) {
        log.info(" >>> item reader filename: [{}]", filename);

        String[] fieldNames = {
            "alertLevel",
            "cdi",
            "field03",
            "field04",
            "field05",
            "feltRecords",
            "field07",
            "field08",
            "magnitude",
            "magType",
            "mmi",
            "field12",
            "field13",
            "place",
            "field15",
            "significance",
            "sourceId",
            "field18",
            "time",
            "field20",
            "tsunami",
            "eventType",
            "field23",
            "field24",
            "field25",
            "field26"
        };

        BeanWrapperFieldSetMapper<UsgsEarthquake> earthquakeFieldsMapper = new BeanWrapperFieldSetMapper<>() {{
            setTargetType(UsgsEarthquake.class);
            setStrict(false); // ignore fields that can't be mapped
        }};

        return new FlatFileItemReaderBuilder<UsgsEarthquake>()
            .name("earthquakeItemReader")
            .resource(new FileSystemResource(filename))
            .delimited()
            .names(fieldNames)
            .fieldSetMapper(earthquakeFieldsMapper)
            .linesToSkip(1) // skip header
            .strict(true) // fail if file does not exist
            .build();
    }
}
