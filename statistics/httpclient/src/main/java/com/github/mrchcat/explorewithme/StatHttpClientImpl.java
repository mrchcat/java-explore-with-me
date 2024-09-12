package com.github.mrchcat.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StatHttpClientImpl implements StatHttpClient {
    private final RestTemplate restTemplate;
    private final String serverUrl;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd%20HH:mm:ss");

    public StatHttpClientImpl(@Value("${statserver.url}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
        log.info("serverUrl={}",serverUrl);
    }

    @Override
    public void addRequest(RequestCreateDto createDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var request = new HttpEntity<>(createDTO, headers);
            URI url = UriComponentsBuilder
                    .fromUriString(serverUrl)
                    .path("/hit")
                    .build()
                    .toUri();
        try {
            restTemplate.exchange(url, HttpMethod.POST,request,Object.class);
        } catch (Exception e) {
            log.error("Statistical service can not add data {}. Stacktrace: {}",
                    createDTO, e.getStackTrace());
        }
    }

    @Override
    public List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto qp) throws IOException {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("start", qp.start.format(formatter));
        queryParams.add("end", qp.end.format(formatter));
        queryParams.addAll("uris", List.of(qp.uris));
        queryParams.add("unique", Boolean.toString(qp.unique));
        URI url = UriComponentsBuilder
                .fromUriString(serverUrl)
                .path("/stats")
                .queryParams(queryParams)
                .build(true)
                .toUri();
        ParameterizedTypeReference<List<RequestStatisticDto>> ptr = new ParameterizedTypeReference<>() {
        };
        try {
            var responseEntity =restTemplate.exchange(url, HttpMethod.GET,null,ptr);
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("""
                    Statistical service did not answer for get request with parameters {}.
                    Stacktrace: {}
                    """, queryParams, e.getStackTrace());
            throw new IOException(e.getMessage());
        }
    }
}
