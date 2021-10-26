package ru.javaprojects.rewardcalculator.web.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.service.UserService;
import ru.javaprojects.rewardcalculator.util.exception.ErrorInfo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JacksonObjectMapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.BAD_TOKEN_ERROR;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.DISABLED_ERROR;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_BAD_TOKEN;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DISABLED;
import static ru.javaprojects.rewardcalculator.web.security.JwtProvider.TOKEN_PREFIX;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    public static final String OPTIONS_HTTP_METHOD = "Options";
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final ObjectMapper mapper = JacksonObjectMapper.getMapper();

    public JwtAuthorizationFilter(JwtProvider jwtProvider, UserService userService) {
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpStatus.OK.value());
        }
        else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                String token = authorizationHeader.substring(TOKEN_PREFIX.length());
                String userEmail = jwtProvider.getSubject(token);

                if (jwtProvider.isTokenValid(userEmail, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    AuthResult authResult = getAuthentication(userEmail, request, response);
                    if (!authResult.enable) {
                        return;
                    } else {
                        SecurityContextHolder.getContext().setAuthentication(authResult.authToken);
                    }
                }
                else {
                    SecurityContextHolder.clearContext();
                }
            } catch (JWTVerificationException e) {
                sendBadTokenResponse(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private AuthResult getAuthentication(String userEmail, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
        try {
            User user = userService.getByEmail(userEmail);
            if (!user.isEnabled()) {
                sendDisableResponse(request, response);
                return new AuthResult(null, false);
            } else {
                AuthorizedUser authUser = new AuthorizedUser(user);
                usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            }
        } catch (NotFoundException e) {
            // We do not need to do anything here, just return AuthResult with null UsernamePasswordAuthenticationToken
        }
        return new AuthResult(usernamePasswordAuthenticationToken, true);
    }

    private void sendDisableResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorInfo responseEntity = new ErrorInfo(request.getRequestURL(), DISABLED_ERROR,
                DISABLED_ERROR.getErrorCode(), EXCEPTION_DISABLED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ServletOutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, responseEntity);
        outputStream.flush();
    }

    private void sendBadTokenResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorInfo responseEntity = new ErrorInfo(request.getRequestURL(), BAD_TOKEN_ERROR,
                BAD_TOKEN_ERROR.getErrorCode(), EXCEPTION_BAD_TOKEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ServletOutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, responseEntity);
        outputStream.flush();
    }

    private static class AuthResult {
        private final UsernamePasswordAuthenticationToken authToken;
        private final boolean enable;

        public AuthResult(UsernamePasswordAuthenticationToken authToken, boolean enable) {
            this.authToken = authToken;
            this.enable = enable;
        }
    }
}