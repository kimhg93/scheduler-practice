package com.practice.scheduler.job.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTPMethod Enum 클래스를 통해 각 메서드별로 API 호출을 실행함
 * 싱글튼으로 재사용 하도록 구현
 */
public class HandleHttpRequest {

    private static HandleHttpRequest instance;

    private HandleHttpRequest(){
    }

    public static HandleHttpRequest getInstance(){
        if(instance == null){
            return new HandleHttpRequest();
        } else return instance;
    }

    public void get(String url, Map<String, Object> param, APICallbackHandler handler) {
        WebClient webClient = WebClient.create();
        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(handler::onSuccess, handler::onError);
    }

    public void post(String url, Map<String, Object> param, APICallbackHandler handler) {
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString())
                .build();

        webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(param)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(handler::onSuccess, handler::onError);
    }

    public void put(String url, Map<String, Object> param, APICallbackHandler handler) {
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString())
                .build();

        webClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(param)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(handler::onSuccess, handler::onError);
    }

    public void delete(String url, Map<String, Object> param, APICallbackHandler handler) {
        WebClient webClient = WebClient.create();
        webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(handler::onSuccess, handler::onError);
    }

}
