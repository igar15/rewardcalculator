package ru.javaprojects.rewardcalculator.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;

import java.time.YearMonth;
import java.util.List;

import static java.time.Month.*;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class PaymentPeriodTestData {
    public static final TestMatcher<PaymentPeriod> PAYMENT_PERIOD_MATCHER = TestMatcher.usingIgnoringFieldsComparator(PaymentPeriod.class);

    public static final int PAYMENT_PERIOD_1_ID = START_SEQ + 15;
    public static final int PAYMENT_PERIOD_2_ID = START_SEQ + 16;
    public static final int PAYMENT_PERIOD_3_ID = START_SEQ + 17;
    public static final int NOT_FOUND = 10;

    public static final PaymentPeriod paymentPeriod1 = new PaymentPeriod(PAYMENT_PERIOD_1_ID, YearMonth.of(2021, JANUARY), 120d);
    public static final PaymentPeriod paymentPeriod2 = new PaymentPeriod(PAYMENT_PERIOD_2_ID, YearMonth.of(2021, FEBRUARY), 150.75);
    public static final PaymentPeriod paymentPeriod3 = new PaymentPeriod(PAYMENT_PERIOD_3_ID, YearMonth.of(2021, MARCH), 176.50);

    public static final Pageable PAGEABLE = PageRequest.of(0, 2);
    public static final Page<PaymentPeriod> PAGE = new PageImpl<>(List.of(paymentPeriod3, paymentPeriod2), PAGEABLE, 3);
    public static final String JSON_PAGE =
            "{\"content\":[{\"id\":100017,\"period\":\"2021-03\",\"requiredHoursWorked\":176.5}," +
            "{\"id\":100016,\"period\":\"2021-02\",\"requiredHoursWorked\":150.75}]," +
            "\"pageable\":{\"page\":0,\"size\":2,\"sort\":{\"orders\":[]}},\"total\":3}";

    public static PaymentPeriod getNew() {
        return new PaymentPeriod(null, YearMonth.now(), 176.25);
    }

    public static PaymentPeriod getUpdated() {
        return new PaymentPeriod(PAYMENT_PERIOD_1_ID, YearMonth.of(2021, APRIL), 174.25);
    }
}