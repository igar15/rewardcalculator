package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Position;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface PositionRepository extends JpaRepository<Position, Integer> {

    List<Position> findAllByDepartmentIdOrderByName(int departmentId);
}