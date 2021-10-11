package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface EmployeeRewardRepository extends JpaRepository<EmployeeReward, Integer> {

    @EntityGraph(attributePaths = "employee")
    List<EmployeeReward> findAllByDepartmentRewardIdOrderByEmployeeName(int departmentRewardId);
}