package ru.javaprojects.rewardcalculator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface DepartmentRewardRepository extends JpaRepository<DepartmentReward, Integer> {

    List<DepartmentReward> findAllByDepartmentIdOrderByPaymentPeriod_PeriodDesc(int departmentId);

    Page<DepartmentReward> findAllByDepartmentIdOrderByPaymentPeriod_PeriodDesc(int departmentId, Pageable pageable);

    Optional<DepartmentReward> findByDepartmentIdAndPaymentPeriodId(int departmentId, int paymentPeriodId);

    @EntityGraph(attributePaths = {"department", "paymentPeriod"})
    @Query("SELECT d FROM DepartmentReward d WHERE d.id = :id")
    DepartmentReward findByIdWithDepartmentAndPaymentPeriod(@Param("id") int id);
}