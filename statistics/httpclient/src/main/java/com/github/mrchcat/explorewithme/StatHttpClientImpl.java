package com.github.mrchcat.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StatHttpClientImpl implements StatHttpClient {
    private final RestTemplate restTemplate;

    public StatHttpClientImpl(@Value("${stats-server.url}") String serverUrl,
                              RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    @Override
    public void addRequest(RequestCreateDto createDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var request = new HttpEntity<>(createDTO, headers);
        try {
            restTemplate.postForObject("/hit", request, Object.class);
        } catch (HttpStatusCodeException e) {
            log.error("Statistical service con not add data {}. Response with code {} and body",
                    createDTO, e.getStatusCode(), e.getResponseBodyAsByteArray());
        }
    }

    @Override
    public List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto qp) throws IOException {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("start", qp.start.toString());
        queryParams.add("end", qp.end.toString());
        queryParams.addAll("uris", List.of(qp.uris));
        queryParams.add("unique", Boolean.toString(qp.unique));

        URI url = UriComponentsBuilder
                .fromPath("/stats")
                .queryParams(queryParams)
                .build()
                .toUri();

        try {
            RequestStatisticDto[] response = restTemplate.getForObject(url, RequestStatisticDto[].class);
            return Optional.ofNullable(response)
                    .map(Arrays::asList)
                    .orElseGet(Collections::emptyList);
        } catch (HttpStatusCodeException e) {
            log.error("""
                    Statistical service did not answer for get request with parameters {}.
                    Response with code {} and body {}
                    """, queryParams, e.getStatusCode(), e.getResponseBodyAsByteArray());
            throw new IOException(e.getStatusCode().toString());
        }
    }
}
