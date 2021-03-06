package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Employee;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @EntityGraph(attributePaths = "position")
    @Query("SELECT e FROM Employee e WHERE e.position.department.id = :departmentId AND e.fired = false ORDER BY e.name")
    List<Employee> findAllNotFiredByPositionDepartmentIdWithPosition(int departmentId);

    @EntityGraph(attributePaths = "position")
    @Query("SELECT e FROM Employee e WHERE e.position.department.id = :departmentId AND e.fired = true ORDER BY e.name")
    List<Employee> findAllFiredByPositionDepartmentIdWithPosition(int departmentId);

    @EntityGraph(attributePaths = "position")
    List<Employee> findAllByPositionDepartmentIdAndFired(int departmentId, boolean fired);

    @EntityGraph(attributePaths = "position.department")
    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Optional<Employee> findByIdWithPositionDepartment(@Param("id") int id);

    @EntityGraph(attributePaths = "position")
    Optional<Employee> findByPositionDepartmentIdAndFiredAndPositionChiefPosition(int departmentId, boolean fired, boolean chiefPosition);
}