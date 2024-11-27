package com.vasquezhouse.earthquakes.ingestion.worker;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EarthquakeItemProcessor implements ItemProcessor<Earthquake, Earthquake> {
    
    @Override
    public Earthquake process(Earthquake item) {
        // todo: implement
        return item;
    }
}
