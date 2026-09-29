package com.michaeldamico.gestionale;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurazioneOrologio {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
