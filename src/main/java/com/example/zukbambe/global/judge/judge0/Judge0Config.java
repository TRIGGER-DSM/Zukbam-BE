package com.example.zukbambe.global.judge.judge0;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class Judge0Config {

    @Bean
    public RestClient judge0RestClient(Judge0Properties judge0Properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(judge0Properties.requestTimeout());
        requestFactory.setReadTimeout(judge0Properties.requestTimeout());

        // Judge0는 chunked로 온 body를 읽지 못해서, 버퍼링해 Content-Length로 보낸다
        return RestClient.builder()
            .baseUrl(judge0Properties.url())
            .requestFactory(new BufferingClientHttpRequestFactory(requestFactory))
            .build();
    }
}
