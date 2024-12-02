package com.vasquezhouse.analytics.analytics_api.earthquakes;

import com.vasquezhouse.analytics.analytics_api.earthquakes.dto.EarthquakeFilter;
import com.vasquezhouse.analytics.analytics_api.earthquakes.dto.EarthquakeStats;
import com.vasquezhouse.analytics.analytics_api.earthquakes.dto.SortDirection;
import com.vasquezhouse.analytics.analytics_api.earthquakes.dto.SortInput;
import com.vasquezhouse.analytics.analytics_api.earthquakes.model.Earthquake;
import com.vasquezhouse.analytics.analytics_api.relay.Connection;
import com.vasquezhouse.analytics.analytics_api.relay.ConnectionCursor;
import com.vasquezhouse.analytics.analytics_api.relay.Edge;
import com.vasquezhouse.analytics.analytics_api.relay.PageInfo;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EarthquakeService {
    
    private static final int DEFAULT_FIRST = 10;
    private static final int MAX_FIRST = 100;
    
    private final EarthquakeRepository repository;
    
    public Connection<Earthquake> getEarthquakes(
        EarthquakeFilter filter, 
        SortInput sort, 
        Integer first,
        Integer last,
        String after,
        String before
    ) {
        int limit = calculateLimit(first, last);

        Specification<Earthquake> filterSpec = createSpecification(filter);
        Sort dataSort = createSort(sort);
        long totalCount = repository.count(filterSpec);
        int effectiveLimit = limit + 1; // to see if there is another page

        List<Earthquake> earthquakes;
        
        if (after != null) {
            Integer afterId = ConnectionCursor.fromCursor(after);
            Specification<Earthquake> afterSpec = filterSpec
                .and((root, query, cb) -> 
                    cb.greaterThan(root.get("id"), afterId));
            
            earthquakes = repository
                .findAll(afterSpec, dataSort)
                .stream()
                .limit(effectiveLimit)
                .toList();
        } else if (before != null) {
            Integer beforeId = ConnectionCursor.fromCursor(before);
            Specification<Earthquake> beforeSpec = filterSpec
                .and((root, query, cb) ->
                    cb.lessThan(root.get("id"), beforeId));

            earthquakes = repository
                .findAll(beforeSpec, dataSort.reverse())
                .stream()
                .limit(effectiveLimit)
                .toList();
            
            if (last != null) {
                earthquakes = new ArrayList<>(earthquakes);
                Collections.reverse(earthquakes);
            }
        } else {
            earthquakes = repository
                .findAll(filterSpec, dataSort)
                .stream()
                .limit(effectiveLimit)
                .toList();
        }
        
        boolean hasMore = earthquakes.size() > limit;
        
        if (hasMore) {
            earthquakes = earthquakes.subList(0, limit);
        }

        List<Edge<Earthquake>> edges = earthquakes
            .stream()
            .map(earthquake -> Edge.<Earthquake>builder()
                .cursor(ConnectionCursor.toCursor(earthquake.getId()))
                .node(earthquake)
                .build()
            )
            .toList();

        PageInfo pageInfo = PageInfo.builder()
            .hasNextPage(hasMore && first != null)
            .hasPreviousPage(hasMore && last != null)
            .startCursor(edges.isEmpty() ? null : edges.getFirst().getCursor())
            .endCursor(edges.isEmpty() ? null : edges.getLast().getCursor())
            .build();
        
        return Connection.<Earthquake>builder()
            .edges(edges)
            .pageInfo(pageInfo)
            .totalCount(totalCount)
            .build();
    }
    
    public EarthquakeStats getStats(EarthquakeFilter filter) {
        Specification<Earthquake> filterSpec = createSpecification(filter);
        List<Earthquake> earthquakes = repository.findAll(filterSpec);

        double avgMagnitude = earthquakes.stream()
            .filter(earthquake -> earthquake.getMagnitude() != null)
            .mapToDouble(earthquake -> earthquake.getMagnitude().doubleValue())
            .average()
            .orElse(0.0);

        double maxMagnitude = earthquakes.stream()
            .filter(earthquake -> earthquake.getMagnitude() != null)
            .mapToDouble(earthquake -> earthquake.getMagnitude().doubleValue())
            .max()
            .orElse(0.0);

        long tsunamiCount = earthquakes.stream()
            .filter(Earthquake::getTsunami)
            .count();
        
        return EarthquakeStats.builder()
            .count(earthquakes.size())
            .averageMagnitude(avgMagnitude)
            .maxMagnitude(maxMagnitude)
            .tsunamiCount(tsunamiCount)
            .build();
    }
    
    private Specification<Earthquake> createSpecification(EarthquakeFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filter.getStartTime() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("time"), filter.getStartTime()));
            }
            if (filter.getEndTime() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("time"), filter.getEndTime()));
            }
            if (filter.getMinMagnitude() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("magnitude"), filter.getMinMagnitude()));
            }
            if (filter.getMaxMagnitude() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("magnitude"), filter.getMaxMagnitude()));
            }
            if (filter.getCountry() != null) {
                predicates.add(builder.equal(root.get("country"), filter.getCountry()));
            }
            if (filter.getTsunami() != null) {
                predicates.add(builder.equal(root.get("tsunami"), filter.getTsunami()));
            }
            
            return predicates.isEmpty()
                ? builder.conjunction()
                : builder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    private Sort createSort(SortInput sort) {
        return Sort.by(
            sort.getDirection() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC,
            sort.getField().toString().toLowerCase()
        );
    }

    private int calculateLimit(Integer first, Integer last) {
        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot specify both first and last");
        }
        
        int limit = first != null
            ? first
            : last != null
                ? last
                : DEFAULT_FIRST;
        
        return Math.min(limit, MAX_FIRST);
    }
}
