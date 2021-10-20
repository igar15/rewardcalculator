package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.YearMonth;
import java.util.List;

import static java.time.Month.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.*;

class PaymentPeriodServiceTest extends AbstractServiceTest {

    @Autowired
    private PaymentPeriodService service;

    @Test
    void create() {
        PaymentPeriod created = service.create(getNew());
        int newId = created.id();
        PaymentPeriod newPaymentPeriod = getNew();
        newPaymentPeriod.setId(newId);
        PAYMENT_PERIOD_MATCHER.assertMatch(created, newPaymentPeriod);
        PAYMENT_PERIOD_MATCHER.assertMatch(service.get(newId), newPaymentPeriod);
    }

    @Test
    void duplicatePeriodCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new PaymentPeriod(null, YearMonth.of(2021, JANUARY), 100d)));
    }

    @Test
    void get() {
        PaymentPeriod paymentPeriod = service.get(PAYMENT_PERIOD_1_ID);
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriod, paymentPeriod1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getAll() {
        List<PaymentPeriod> paymentPeriods = service.getAll();
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriods, paymentPeriod3, paymentPeriod2, paymentPeriod1);
    }

    @Test
    void getAllByPage() {
        Page<PaymentPeriod> paymentPeriods = service.getAll(PAGEABLE);
        assertEquals(PAGE, paymentPeriods);
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriods.getContent(), paymentPeriod3, paymentPeriod2);
    }

    @Test
    void delete() {
        service.delete(PAYMENT_PERIOD_1_ID);
        assertThrows(NotFoundException.class, () -> service.get(PAYMENT_PERIOD_1_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdated());
        PAYMENT_PERIOD_MATCHER.assertMatch(service.get(PAYMENT_PERIOD_1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        PaymentPeriod updated = getUpdated();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PaymentPeriod(null, null, 100d)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PaymentPeriod(null, YearMonth.of(2021, MAY), null)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PaymentPeriod(null, YearMonth.of(2021, MAY), 205d)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PaymentPeriod(null, YearMonth.of(2021, MAY), -0.5)));
    }
}