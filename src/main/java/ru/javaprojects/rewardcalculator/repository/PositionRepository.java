package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Position;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface PositionRepository extends JpaRepository<Position, Integer> {

    List<Position> findAllByDepartmentIdOrderByName(int departmentId);

    @EntityGraph(attributePaths = {"department"})
    @Query("SELECT p FROM Position p WHERE p.id = :id")
    Optional<Position> findByIdWithDepartment(@Param("id") int id);
}