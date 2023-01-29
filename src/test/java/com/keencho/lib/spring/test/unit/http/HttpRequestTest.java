package com.keencho.lib.spring.test.unit.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

public class HttpRequestTest {

    @Test
    @DisplayName("Java Http Client")
    public void javaHttpClient() throws IOException, InterruptedException {
        var mapper = new ObjectMapper();
        var client = HttpClient.newHttpClient();

        var request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .GET()
                .build();

        var res = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(mapper.readValue(res.body(), ArrayList.class));
    }
}
