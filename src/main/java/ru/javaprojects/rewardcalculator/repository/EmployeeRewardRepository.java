package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface EmployeeRewardRepository extends JpaRepository<EmployeeReward, Integer> {

    @EntityGraph(attributePaths = "employee")
    List<EmployeeReward> findAllByDepartmentRewardIdOrderByEmployeeName(int departmentRewardId);


    @EntityGraph(attributePaths = {"employee.position", "departmentReward.paymentPeriod"})
    Optional<EmployeeReward> findById(int id);
}