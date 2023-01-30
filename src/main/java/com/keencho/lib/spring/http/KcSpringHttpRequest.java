package com.keencho.lib.spring.http;

import com.keencho.lib.spring.common.exception.KcRuntimeException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public interface KcSpringHttpRequest {

    <T> T call(String url, HttpMethod method, MultiValueMap<String, String> header, MultiValueMap<String, String> queryString, Object body, Class<T> clazz);

    <T> T call(String url, HttpMethod method, MultiValueMap<String, String> header, MultiValueMap<String, String> queryString, Object body, ParameterizedTypeReference<T> reference);

    default <T> T get(String url, MultiValueMap<String, String> header, Class<T> clazz) {
        return this.call(url, HttpMethod.GET, header, null, null, clazz);
    }

    default <T> T get(String url, MultiValueMap<String, String> header, ParameterizedTypeReference<T> reference) {
        return this.call(url, HttpMethod.GET, header, null, null, reference);
    }

    default <T> T get(String url, MultiValueMap<String, String> header, MultiValueMap<String, String> queryString, Class<T> clazz) {
        return this.call(url, HttpMethod.GET, header, queryString, null, clazz);
    }

    default <T> T get(String url, MultiValueMap<String, String> header, MultiValueMap<String, String> queryString, ParameterizedTypeReference<T> reference) {
        return this.call(url, HttpMethod.GET, header, queryString, null, reference);
    }

    default <T> T post(String url, MultiValueMap<String, String> header, Object body, Class<T> clazz) {
        return this.call(url, HttpMethod.POST, header, null, body, clazz);
    }

    default <T> T post(String url, MultiValueMap<String, String> header, Object body, ParameterizedTypeReference<T> reference) {
        return this.call(url, HttpMethod.POST, header, null, body, reference);
    }

    default <T> T put(String url, MultiValueMap<String, String> header, Object body, Class<T> clazz) {
        return this.call(url, HttpMethod.PUT, header, null, body, clazz);
    }

    default <T> T put(String url, MultiValueMap<String, String> header, Object body, ParameterizedTypeReference<T> clazz) {
        return this.call(url, HttpMethod.PUT, header, null, body, clazz);
    }

    default <T> T delete(String url, MultiValueMap<String, String> header, Object body, Class<T> clazz) {
        return this.call(url, HttpMethod.DELETE, header, null, body, clazz);
    }

    default <T> T delete(String url, MultiValueMap<String, String> header, Object body, ParameterizedTypeReference<T> reference) {
        return this.call(url, HttpMethod.DELETE, header, null, body, reference);
    }

    default String buildQueryString(String url, MultiValueMap<String, String> queryString) {

        if (queryString == null || queryString.isEmpty()) return url;

        if (url.contains("?")) {
            throw new KcRuntimeException("url already contains query string!");
        }

        return url + ("?" + String.join("&", queryString.entrySet().stream().map(p -> p.getKey() + "=" + p.getValue()).toArray(String[]::new)));
    }

    static MultiValueMap<String, String> createMultiValueMap(String... args) {

        if (args == null || args.length == 0 || args.length % 2 != 0) {
            throw new KcRuntimeException("cannot create multi value map!");
        }

        var map = new LinkedMultiValueMap<String, String>();

        for (var i = 0; i < args.length; i += 2) {
            map.add(args[i], args[i + 1]);
        }

        return map;
    }
}
