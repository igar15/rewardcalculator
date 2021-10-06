package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;
import ru.javaprojects.rewardcalculator.repository.PaymentPeriodRepository;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class PaymentPeriodService {
    private final PaymentPeriodRepository repository;

    public PaymentPeriodService(PaymentPeriodRepository repository) {
        this.repository = repository;
    }

    public PaymentPeriod create(PaymentPeriod paymentPeriod) {
        Assert.notNull(paymentPeriod, "paymentPeriod must not be null");
        return repository.save(paymentPeriod);
    }

    public PaymentPeriod get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found payment period with id=" + id));
    }

    public List<PaymentPeriod> getAll() {
        return repository.findAllByOrderByPeriodDesc();
    }

    public void delete(int id) {
        PaymentPeriod paymentPeriod = get(id);
        repository.delete(paymentPeriod);
    }

    public void update(PaymentPeriod paymentPeriod) {
        Assert.notNull(paymentPeriod, "paymentPeriod must not be null");
        get(paymentPeriod.id());
        repository.save(paymentPeriod);
    }
}