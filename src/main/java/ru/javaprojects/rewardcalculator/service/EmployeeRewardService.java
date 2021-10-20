package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.repository.EmployeeRewardRepository;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

import static ru.javaprojects.rewardcalculator.util.EmployeeRewardUtil.*;

@Service
public class EmployeeRewardService {
    private static final double PREMIUM_RATE = 0.3;
    private final EmployeeRewardRepository repository;
    private final DepartmentRewardService departmentRewardService;

    public EmployeeRewardService(EmployeeRewardRepository repository, DepartmentRewardService departmentRewardService) {
        this.repository = repository;
        this.departmentRewardService = departmentRewardService;
    }

    public EmployeeReward get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found employee reward with id=" + id));
    }

    public List<EmployeeReward> getAllByDepartmentRewardId(int departmentRewardId) {
        departmentRewardService.get(departmentRewardId);
        return repository.findAllByDepartmentRewardIdOrderByEmployeeName(departmentRewardId);
    }

    @Transactional
    public void update(EmployeeRewardTo employeeRewardTo) {
        checkToState(employeeRewardTo);
        EmployeeReward employeeReward = repository.findByIdWithPositionAndDepartmentReward(employeeRewardTo.getId())
                .orElseThrow(() -> new NotFoundException("Not found employee reward with id=" + employeeRewardTo.getId()));
        DepartmentReward departmentReward = employeeReward.getDepartmentReward();
        int salary = employeeReward.getEmployee().getPosition().getSalary();
        double requiredHoursWorked = departmentReward.getPaymentPeriod().getRequiredHoursWorked();

        int hoursWorkedReward = calculateHoursWorkedReward(employeeRewardTo.getHoursWorked(), salary, requiredHoursWorked);
        int newFullReward = calculateFullReward(hoursWorkedReward, employeeRewardTo.getAdditionalReward(), employeeRewardTo.getPenalty());
        int newDistributedAmount = calculateNewDistributedAmount(departmentReward, employeeReward.getFullReward(), newFullReward);
        updateFromTo(employeeReward, employeeRewardTo, hoursWorkedReward);
        departmentReward.setDistributedAmount(newDistributedAmount);
    }
}