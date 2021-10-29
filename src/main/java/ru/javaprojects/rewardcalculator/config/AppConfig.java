package ru.javaprojects.rewardcalculator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.javaprojects.rewardcalculator.util.EmployeeRewardUtil;
import ru.javaprojects.rewardcalculator.web.json.JacksonObjectMapper;

import javax.annotation.PostConstruct;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }

    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //Need for initialization static class variables of EmployeeRewardUtil class on application startup
    //to check the correct loading of the required pdf font and properties
    @PostConstruct
    public void checkEmployeeRewardUtilClass() throws ClassNotFoundException {
        Class.forName(EmployeeRewardUtil.class.getName());
    }
}