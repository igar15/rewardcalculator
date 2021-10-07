package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface DepartmentRewardRepository extends JpaRepository<DepartmentReward, Integer> {

    List<DepartmentReward> findAllByDepartmentIdOrderByPaymentPeriod_PeriodDesc(int departmentId);

    Optional<DepartmentReward> findByDepartmentIdAndPaymentPeriodId(int departmentId, int paymentPeriodId);
}