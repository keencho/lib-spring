package com.keencho.lib.spring.test.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keencho.lib.spring.http.KcJavaHttpClient;
import com.keencho.lib.spring.http.KcSpringHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.core.ParameterizedTypeReference;

import java.net.http.HttpClient;
import java.util.List;

public class HttpRequestTestBase {

    public static class Item {
        int userId;
        int id;
        String title;
        String body;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public static String baseUrl;

    public static KcJavaHttpClient javaHttpClient;

    @BeforeAll
    public static void setUp() {
        baseUrl = "https://jsonplaceholder.typicode.com";
        javaHttpClient = new KcJavaHttpClient(HttpClient.newHttpClient(), new ObjectMapper());
    }

}
