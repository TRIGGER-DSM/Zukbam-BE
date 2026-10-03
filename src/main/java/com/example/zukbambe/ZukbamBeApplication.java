package com.example.zukbambe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZukbamBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZukbamBeApplication.class, args);
    }

}
