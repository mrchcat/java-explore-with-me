package com.github.mrchcat.explorewithme.utils.views;

import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;
import com.github.mrchcat.explorewithme.StatHttpClient;
import com.github.mrchcat.explorewithme.event.model.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
@Slf4j
public class ViewsImpl implements Views {

    private final StatHttpClient statHttpClient;
    private static final boolean IS_UNIQUE_VIEWS = true;
    private static final String PUBLIC_VIEW_URI = "/events";

    @Override
    public long getEventViews(Event event) {
        LocalDateTime start = event.getCreatedOn();
        String uri = makeUri(event.getId());
        String[] uris = new String[]{uri};
        var uriViewsMap = getRequestFromStat(start, uris);
        return (uriViewsMap.containsKey(uri)) ? uriViewsMap.get(uri) : 0;
    }

    @Override
    public Map<Long, Long> getEventViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, String> idUrisMap = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(Function.identity(), this::makeUri));

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new RuntimeException("Event entity has no creation date"));

        Map<String, Long> uriViewsMap = getRequestFromStat(start, idUrisMap.values().toArray(new String[0]));

        Map<Long, Long> idViewMap = new HashMap<>(uriViewsMap.size());
        for (var entry : idUrisMap.entrySet()) {
            Long id = entry.getKey();
            String uri = entry.getValue();
            idViewMap.put(id, (uriViewsMap.containsKey(uri)) ? uriViewsMap.get(uri) : 0);
        }
        return idViewMap;
    }

    private Map<String, Long> getRequestFromStat(LocalDateTime start, String[] uris) {
        var request = RequestQueryParamDto.builder()
                .start(start)
                .end(LocalDateTime.now())
                .uris(uris)
                .unique(IS_UNIQUE_VIEWS)
                .build();
        try {
            List<RequestStatisticDto> answer = statHttpClient.getRequestStatistic(request);
            return answer.stream()
                    .collect(Collectors.toMap(RequestStatisticDto::getUri, RequestStatisticDto::getHits));
        } catch (IOException ex) {
            log.error("Stat service returned an exception for request {}", request);
            return Stream.of(uris).collect(Collectors.toMap(Function.identity(), u -> 0L));
        }
    }

    private String makeUri(long id) {
        return PUBLIC_VIEW_URI + "/" + id;
    }
}
