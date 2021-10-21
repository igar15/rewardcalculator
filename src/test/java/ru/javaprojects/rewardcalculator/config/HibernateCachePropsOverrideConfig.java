package ru.javaprojects.rewardcalculator.config;

import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class HibernateCachePropsOverrideConfig {

    @Bean
    public PropertyOverrideConfigurer propertyOverrideConfigurer() {
        PropertyOverrideConfigurer propertyOverrideConfigurer = new PropertyOverrideConfigurer();
        propertyOverrideConfigurer.setLocation(new ClassPathResource("hibernateCachePropsOverride.cfg"));
        return propertyOverrideConfigurer;
    }
}