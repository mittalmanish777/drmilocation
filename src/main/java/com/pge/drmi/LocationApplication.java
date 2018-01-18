package com.pge.drmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.pge.drmi.config.LSEMapping;


@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(LSEMapping.class)
public class LocationApplication  {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LocationApplication.class, args);
    
    }
}