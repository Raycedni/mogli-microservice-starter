package com.mogli.microservicebase.lifecycle;

import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.core.io.ClassPathResource;

public class MogliApp {
    public SpringApplicationBuilder setup(Class<?> springApplicationClass){
        return new SpringApplicationBuilder(springApplicationClass)
                .banner(new ResourceBanner(new ClassPathResource("banner.txt")));
    }
}
