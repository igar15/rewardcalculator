package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface PaymentPeriodRepository extends JpaRepository<PaymentPeriod, Integer> {

    List<PaymentPeriod> findAllByOrderByPeriodDesc();
}