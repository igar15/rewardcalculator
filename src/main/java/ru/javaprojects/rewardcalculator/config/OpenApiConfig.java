package ru.javaprojects.rewardcalculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.ALLOWED_ADMIN;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Reward Calculating System App Web Service Documentation",
                version = "1.0",
                description = "This page documents Reward Calculating System RESTful Web Service endpoints<br><br>" +
                        "To get Authorization JWT token use Profile Controller login endpoint (credentials: admin@gmail.com/admin)",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(url = "https://javaprojects.ru", name = "Igor Shlyakhtenkov", email = "ishlyakhtenkov@yandex.ru")
        ),
        tags = {@Tag(name = "Profile Controller"),
                @Tag(name = "Employee Reward Controller"),
                @Tag(name = "Department Reward Controller"),
                @Tag(name = "Payment Period Controller"),
                @Tag(name = "Department Controller"),
                @Tag(name = "Position Controller"),
                @Tag(name = "Employee Controller"),
                @Tag(name = "User Controller" + ALLOWED_ADMIN)},
        security = @SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {
        public static final String ALLOWED_ADMIN = " (Allowed: ADMIN)";
        public static final String ALLOWED_ADMIN_PERSONNEL_OFFICER = " (Allowed: ADMIN, PERSONNEL_OFFICER)";
        public static final String ALLOWED_ADMIN_ECONOMIST = " (Allowed: ADMIN, ECONOMIST)";
        public static final String ALLOWED_ADMIN_DEPARTMENT_HEAD = " (Allowed: ADMIN, DEPARTMENT_HEAD)";
        public static final String ALLOWED_ADMIN_PERSONNEL_OFFICER_ECONOMIST = " (Allowed: ADMIN, PERSONNEL_OFFICER, ECONOMIST)";
        public static final String ALLOWED_ADMIN_ECONOMIST_DEPARTMENT_HEAD = " (Allowed: ADMIN, ECONOMIST, DEPARTMENT_HEAD)";
        @Bean
        public GroupedOpenApi api() {
                return GroupedOpenApi.builder()
                        .group("REST API")
                        .pathsToMatch("/api/**")
                        .build();
        }
}