package com.keencho.lib.spring.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keencho.lib.spring.common.exception.KcHttpException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KcJavaHttpClient implements KcSpringHttpClient {

    final HttpClient client;
    final ObjectMapper objectMapper;

    public KcJavaHttpClient(HttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    private String call(String url, HttpMethod method, MultiValueMap<String, String> header, MultiValueMap<String, String> queryString, Object body) {
        if (method == HttpMethod.GET) {
            url = this.buildQueryString(url, queryString);
        }

        var builder = HttpRequest
                .newBuilder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .uri(URI.create(url));

        if (header != null && !header.isEmpty()) {
            builder.headers(header.values().toArray(String[]::new));
        }

        if (method == HttpMethod.GET) {
            builder.GET();
        } else {
            try {
                builder.method(
                        method.name(),
                        body == null
                                ? HttpRequest.BodyPublishers.noBody()
                                : HttpRequest.BodyPublishers.ofString(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body))
                );
            } catch (JsonProcessingException e) {
                throw new KcHttpException("cannot convert value as string");
            }
        }

        HttpResponse<String> response;

        try {
            response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new KcHttpException(e.getMessage());
        }

        if (response == null) {
            throw new KcHttpException("response is null");
        }

        if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
            throw new KcHttpException(String.format("status code is not 2xxSuccessful (%d)", response.statusCode()));
        }

        return response.body();
    }

    @Override
    public <T> T call(String url, HttpMethod method, MultiValueMap<String, String> header, MultiValueMap<String, String> queryString, Object body, Class<T> clazz) {
       var response = this.call(url, method, header, queryString, body);

        try {
            return objectMapper.readValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new KcHttpException(String.format("cannot convert response body to target class (%s)", clazz.getSimpleName()));
        }
    }

    @Override
    public <T> T call(String url, HttpMethod method, MultiValueMap<String, String> header, MultiValueMap<String, String> queryString, Object body, ParameterizedTypeReference<T> reference) {

        var response = this.call(url, method, header, queryString, body);

        try {
            return objectMapper.readValue(response, new TypeReference<>() {
                @Override
                public Type getType() {
                    return reference.getType();
                }
            });
        } catch (JsonProcessingException e) {
            throw new KcHttpException(String.format("cannot convert response body to target class (%s)", reference.getType().getTypeName()));
        }
    }
}
