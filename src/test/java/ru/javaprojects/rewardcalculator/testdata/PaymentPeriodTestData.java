package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;

import java.time.YearMonth;

import static java.time.Month.*;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class PaymentPeriodTestData {
    public static final TestMatcher<PaymentPeriod> PAYMENT_PERIOD_MATCHER = TestMatcher.usingIgnoringFieldsComparator(PaymentPeriod.class);

    public static final int PAYMENT_PERIOD_1_ID = START_SEQ + 11;
    public static final int PAYMENT_PERIOD_2_ID = START_SEQ + 12;
    public static final int PAYMENT_PERIOD_3_ID = START_SEQ + 13;
    public static final int NOT_FOUND = 10;

    public static final PaymentPeriod paymentPeriod1 = new PaymentPeriod(PAYMENT_PERIOD_1_ID, YearMonth.of(2021, JANUARY), 120d);
    public static final PaymentPeriod paymentPeriod2 = new PaymentPeriod(PAYMENT_PERIOD_2_ID, YearMonth.of(2021, FEBRUARY), 150.75);
    public static final PaymentPeriod paymentPeriod3 = new PaymentPeriod(PAYMENT_PERIOD_3_ID, YearMonth.of(2021, MARCH), 176.50);

    public static PaymentPeriod getNew() {
        return new PaymentPeriod(null, YearMonth.now(), 176.25);
    }

    public static PaymentPeriod getUpdated() {
        return new PaymentPeriod(PAYMENT_PERIOD_1_ID, YearMonth.of(2021, APRIL), 174.25);
    }
}