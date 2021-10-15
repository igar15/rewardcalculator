package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Employee;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @EntityGraph(attributePaths = "position")
    List<Employee> findAllByPositionDepartmentId(int departmentId);

    @EntityGraph(attributePaths = "position")
    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Employee findByIdWithPosition(@Param("id") int id);
}