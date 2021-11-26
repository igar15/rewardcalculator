package ru.javaprojects.rewardcalculator.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.rewardcalculator.service.UserService;
import ru.javaprojects.rewardcalculator.web.controller.AbstractControllerTest;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.ADMIN_MAIL;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.DEPARTMENT_HEAD_MAIL;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_BAD_TOKEN;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DISABLED;
import static ru.javaprojects.rewardcalculator.web.security.JwtProvider.*;

class JwtAuthorizationFilterTest extends AbstractControllerTest {
    private static final String REST_URL = "/api/users/";

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    private HttpHeaders departmentHeadJwtHeader;

    private HttpHeaders adminJwtHeader;

    private HttpHeaders adminJwtExpiredHeader;

    private HttpHeaders adminJwtInvalidHeader;

    @PostConstruct
    private void postConstruct() {
        String secretKey = environment.getProperty("jwt.secretKey");
        departmentHeadJwtHeader = getHeaders(jwtProvider.generateAuthorizationToken(DEPARTMENT_HEAD_MAIL));
        adminJwtHeader = getHeaders(jwtProvider.generateAuthorizationToken(ADMIN_MAIL));
        adminJwtExpiredHeader = getHeaders(generateCustomToken(ADMIN_MAIL, (new Date(System.currentTimeMillis() - 10000)), secretKey));
        adminJwtInvalidHeader = getHeaders(generateCustomToken(ADMIN_MAIL, (new Date(System.currentTimeMillis() + AUTHORIZATION_TOKEN_EXPIRATION_TIME)), UUID.randomUUID().toString()));
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return httpHeaders;
    }

    private String generateCustomToken(String userEmail, Date expirationDate, String secretKey) {
        return JWT.create()
                .withIssuer(JAVA_PROJECTS)
                .withAudience(REWARD_CALCULATOR_AUDIENCE)
                .withIssuedAt(new Date())
                .withSubject(userEmail)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC512(secretKey));
    }

    @Test
    void getAllUsers() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .headers(adminJwtHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(personnelOfficer, departmentHead, economist, admin));
    }

    @Test
    void getAllUsersForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .headers(departmentHeadJwtHeader))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    void getAllUsersDisabled() throws Exception {
        userService.enable(ADMIN_ID, false);

        perform(MockMvcRequestBuilders.get(REST_URL)
                .headers(adminJwtHeader))
                .andExpect(status().isForbidden())
                .andExpect(errorType(DISABLED_ERROR))
                .andExpect(detailMessage(EXCEPTION_DISABLED));
    }

    @Test
    void getAllUsersTokenExpired() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .headers(adminJwtExpiredHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }

    @Test
    void getAllUsersTokenInvalid() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .headers(adminJwtInvalidHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }
}