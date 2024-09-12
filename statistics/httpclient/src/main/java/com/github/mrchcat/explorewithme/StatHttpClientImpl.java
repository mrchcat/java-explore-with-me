package com.github.mrchcat.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

@Controller
@Slf4j
public class StatHttpClientImpl implements StatHttpClient {
    private final RestTemplate restTemplate;
    private final String serverUrl;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd%20HH:mm:ss");

    public StatHttpClientImpl(@Value("${statserver.url}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    @Override
    public void addRequest(RequestCreateDto createDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var request = new HttpEntity<>(createDTO, headers);
        try {
            URI url = UriComponentsBuilder
                    .fromUriString(serverUrl)
                    .path("/hit")
                    .build()
                    .toUri();
            restTemplate.postForEntity(url, request, Object.class);

        } catch (HttpStatusCodeException e) {
            log.error("Statistical service can not add data {}. Response with code {} and body {}",
                    createDTO, e.getStatusCode(), e.getResponseBodyAs(String.class));
        } catch (ResourceAccessException e) {
            log.error("Statistical service service is unavailable");
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
        try {
            RequestStatisticDto[] response = restTemplate.getForObject(url, RequestStatisticDto[].class);
            return Optional.ofNullable(response)
                    .map(Arrays::asList)
                    .orElseGet(Collections::emptyList);

        } catch (HttpStatusCodeException e) {
            log.error("""
                    Statistical service did not answer for get request with parameters {}.
                    Response with code {} and body {}
                    """, queryParams, e.getStatusCode(), e.getResponseBodyAs(String.class));
            throw new IOException(e.getStatusCode().toString());
        } catch (ResourceAccessException e) {
            log.error("Statistical service service is unavailable");
            throw new IOException("Statistical service service is unavailable");
        }
    }
}
