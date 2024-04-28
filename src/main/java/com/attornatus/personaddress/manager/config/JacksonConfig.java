package com.attornatus.personaddress.manager.config;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }
}
