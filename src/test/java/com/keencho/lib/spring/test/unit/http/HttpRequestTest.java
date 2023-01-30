package com.keencho.lib.spring.test.unit.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keencho.lib.spring.http.KcJavaHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.net.http.HttpClient;
import java.util.List;

public class HttpRequestTest {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        int userId;
        int id;
        String title;
        String body;

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    @Test
    @DisplayName("Java Http Client")
    public void javaHttpClient() {

        var client = new KcJavaHttpClient(HttpClient.newHttpClient(), new ObjectMapper());

        var url = "https://jsonplaceholder.typicode.com/posts";

        var res = client.get(url, null, new ParameterizedTypeReference<List<Item>>() {});

        System.out.println(res);
    }
}
