package ru.javaprojects.rewardcalculator.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class JwtProvider {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_TOKEN_HEADER = "Authorization-Token";
    public static final String JAVA_PROJECTS = "javaprojects.ru";
    public static final String REWARD_CALCULATOR_AUDIENCE = "Reward Calculator System";
    public static final long AUTHORIZATION_TOKEN_EXPIRATION_TIME = 432_000_000; // 5 days
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

    @Autowired
    private Environment environment;

    public  String generateAuthorizationToken(String userEmail) {
        return JWT.create()
                .withIssuer(JAVA_PROJECTS)
                .withAudience(REWARD_CALCULATOR_AUDIENCE)
                .withIssuedAt(new Date())
                .withSubject(userEmail)
                .withExpiresAt(new Date(System.currentTimeMillis() + AUTHORIZATION_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(Objects.requireNonNull(environment.getProperty("jwt.secretKey"))));
    }

    public String getSubject(String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        return jwtVerifier.verify(token).getSubject();
    }

    public boolean isTokenValid(String userEmail, String token) {
        return !userEmail.isEmpty() && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        Date expirationDate = jwtVerifier.verify(token).getExpiresAt();
        return expirationDate.before(new Date());
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier = null;
        try {
            verifier = JWT.require(Algorithm.HMAC512(Objects.requireNonNull(environment.getProperty("jwt.secretKey")))).build();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
}