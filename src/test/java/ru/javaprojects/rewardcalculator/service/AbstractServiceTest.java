package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.javaprojects.rewardcalculator.TimingExtension;
import ru.javaprojects.rewardcalculator.util.ValidationUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
@ExtendWith(TimingExtension.class)
public abstract class AbstractServiceTest {

    public <T extends Throwable> void validateRootCause(Class<T> rootExceptionClass, Runnable runnable) {
        assertThrows(rootExceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw ValidationUtil.getRootCause(e);
            }
        });
    }
}