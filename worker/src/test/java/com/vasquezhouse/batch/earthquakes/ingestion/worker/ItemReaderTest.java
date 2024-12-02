package com.vasquezhouse.batch.earthquakes.ingestion.worker;

import com.vasquezhouse.batch.earthquakes.ingestion.worker.configuration.ItemReaderConfig;
import com.vasquezhouse.batch.earthquakes.ingestion.worker.domain.UsgsEarthquake;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemReaderTest {
    
    @TempDir
    Path tempDir;
    
    @SneakyThrows
    @Test
    void reads() {
        // arrange
        String csvContent = """
            alert,cdi,code,detail,dmin,felt,gap,ids,mag,magType,mmi,net,nst,place,rms,sig,sources,status,time,title,tsunami,type,types,tz,updated,url
            ,,37389218,https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=ci37389218&format=geojson,0.008693,,85.0,",ci37389218,",1.35,ml,,ci,26.0,"9km NE of Aguanga, CA",0.19,28,",ci,",automatic,1539475168010,"M 1.4 - 9km NE of Aguanga, CA",0,earthquake,",geoserve,nearby-cities,origin,phase-data,",-480.0,1539475395144,https://earthquake.usgs.gov/earthquakes/eventpage/ci37389218
            ,4.4,37389194,https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=ci37389194&format=geojson,0.02137,28.0,21.0,",ci37389194,",3.42,ml,,ci,111.0,"8km NE of Aguanga, CA",0.22,192,",ci,",automatic,1539475062610,"M 3.4 - 8km NE of Aguanga, CA",0,earthquake,",dyfi,focal-mechanism,geoserve,nearby-cities,origin,phase-data,",-480.0,1539536756176,https://earthquake.usgs.gov/earthquakes/eventpage/ci37389194
            """;
        
        FlatFileItemReader<UsgsEarthquake> itemReader = createItemReader(csvContent);

        // act
        UsgsEarthquake firstEarthquake = itemReader.read();
        UsgsEarthquake secondEarthquake = itemReader.read();

        // assert
        assertThat(itemReader.read()).isNull();
        itemReader.close();

        assertThat(firstEarthquake).isNotNull();
        assertThat(firstEarthquake.getAlertLevel()).isEqualTo("");
        assertThat(firstEarthquake.getEventType()).isEqualTo("earthquake");
        assertThat(firstEarthquake.getCdi()).isNull();
        assertThat(firstEarthquake.getMagnitude()).isEqualTo(BigDecimal.valueOf(1.35));
        // add remaining properties to check

        assertThat(secondEarthquake).isNotNull();
        assertThat(secondEarthquake.getAlertLevel()).isEqualTo("");
        assertThat(secondEarthquake.getEventType()).isEqualTo("earthquake");
        // add remaining properties to check
    }

    private FlatFileItemReader<UsgsEarthquake> createItemReader(String csvContent) {

        String testFilePath = tempDir.resolve("test-earthquakes.csv").toString();
        try {
            Files.writeString(Path.of(testFilePath), csvContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FlatFileItemReader<UsgsEarthquake> itemReader = new ItemReaderConfig().itemReader(testFilePath);
        itemReader.open(new ExecutionContext());
        return itemReader;
    }
}
