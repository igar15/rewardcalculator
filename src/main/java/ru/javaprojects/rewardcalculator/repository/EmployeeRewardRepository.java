package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface EmployeeRewardRepository extends JpaRepository<EmployeeReward, Integer> {

    @EntityGraph(attributePaths = "employee.position")
    List<EmployeeReward> findAllByDepartmentRewardIdOrderByEmployeeName(int departmentRewardId);

    @EntityGraph(attributePaths = "departmentReward.paymentPeriod")
    @Query("SELECT e FROM EmployeeReward e WHERE e.id = :id")
    Optional<EmployeeReward> findByIdWithDepartmentReward(int id);

    @EntityGraph(attributePaths = {"departmentReward.department"})
    @Query("SELECT e FROM EmployeeReward e WHERE e.id = :id")
    Optional<EmployeeReward> findByIdWithDepartment(int id);
}