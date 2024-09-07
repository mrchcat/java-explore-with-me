package com.github.mrchcat.explorewithme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatHttpClient extends BaseHttpClient {
    private static final String API_PREFIX = "";

    public StatHttpClient(@Value("${stats-server.url}") String serverUrl,
                          RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public void getRequestStatistic(RequestCreateDTO createDTO) {
        String path = "/hit";
        post(path, createDTO);
    }

    public ResponseEntity<Object> getRequestStatistic(LocalDateTime start,
                                                      LocalDateTime end,
                                                      List<String> uris,
                                                      boolean unique) {
        String path = "/stats";
        Map<String, Object> query = new HashMap<>();
        query.put("start", start);
        query.put("end", end);
        query.put("uris", uris);
        query.put("unique", unique);
        return get(path, query);
    }
}
