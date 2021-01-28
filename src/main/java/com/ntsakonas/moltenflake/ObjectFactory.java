package com.ntsakonas.moltenflake;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class ObjectFactory {

    @Bean
    public Clock getSystemClock() {
        return Clock.systemUTC();
    }
}
