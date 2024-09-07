package com.github.mrchcat.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseHttpClient {
    private final RestTemplate rest;

    public BaseHttpClient(RestTemplate rest) {
        this.rest = rest;
    }

    public ResponseEntity<Object> get(String path, Map<String, Object> query) {
        return makeAndSendRequest(HttpMethod.GET, path,  query, null);
    }

    public <T> ResponseEntity<Object> post(String path,T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          Map<String, Object> query,
                                                          T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Object> serverResponse;
        try {
            UriComponents uriComponents;
            if (query == null) {
                uriComponents = UriComponentsBuilder.fromPath(path).build();
            } else {
                MultiValueMap<String, String> queryMap = new LinkedMultiValueMap<>();
                for (var entry : query.entrySet()) {
                    queryMap.add(entry.getKey(), entry.getValue().toString());
                }
                uriComponents = UriComponentsBuilder.fromPath(path).queryParams(queryMap).build();
            }
            String uri = uriComponents.toString();
            serverResponse = rest.exchange(uri, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
