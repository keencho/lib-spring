package com.keencho.lib.spring.test.unit.http;

import com.keencho.lib.spring.http.KcSpringHttpClient;
import com.keencho.lib.spring.test.base.HttpRequestTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest extends HttpRequestTestBase {

    private void listAll(KcSpringHttpClient client) {
        var res = client.get(baseUrl + "/posts", null, new ParameterizedTypeReference<List<Item>>() {});

        assertTrue(!res.isEmpty());
    }

    private void findOne(KcSpringHttpClient client, int id) {
        var res = client.get(baseUrl + "/posts/" + id, null, Item.class);

        assertNotNull(res);
        assertEquals(res.getId(), id);
    }

    private void filteringResources(KcSpringHttpClient client, int userId) {
        var qs = KcSpringHttpClient.createMultiValueMap("userId", String.valueOf(userId));
        var res = client.get(baseUrl + "/posts", null, qs, new ParameterizedTypeReference<List<Item>>() {});

        assertNotNull(res);
        assertTrue(res.stream().allMatch(item -> item.getUserId() == userId));
    }

    private void createResource(KcSpringHttpClient client) {
        var map = new HashMap<String, Object>();
        map.put("userId", 4);
        map.put("title", "This is Title");
        map.put("body", "This is Body");

        var res = client.post(baseUrl + "/posts", null, map, Item.class);

        assertNotNull(res);
        assertEquals(res.getUserId(), 4);
        assertEquals(res.getTitle(), "This is Title");
        assertNotEquals(res.getBody(), "THIS IS BODY");
    }

    private void updateResource(KcSpringHttpClient client) {
        var map = new HashMap<String, Object>();
        map.put("id", 3);
        map.put("title", "foo");
        map.put("body", "bar");
        map.put("userId", 5);

        var res = client.put(baseUrl + "/posts/3", null, map, Item.class);

        assertNotNull(res);
        assertEquals(res.getId(), 3);
        assertEquals(res.getUserId(), 5);
        assertEquals(res.getTitle(), "foo");
        assertEquals(res.getBody(), "bar");
    }

    @Test
    @DisplayName("Java Http Client")
    public void javaHttpClient() {
        var client = javaHttpClient;

        listAll(client);
        findOne(client, 2);
        filteringResources(client, 5);
        createResource(client);
        updateResource(client);
    }
}
