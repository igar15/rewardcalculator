package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.model.Rate;
import ru.javaprojects.rewardcalculator.repository.EmployeeRewardRepository;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.util.EmployeeUtil.EmployeeSignature;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

import static ru.javaprojects.rewardcalculator.util.EmployeeRewardUtil.*;

@Service
public class EmployeeRewardService {
    private static final double PREMIUM_RATE = 0.3;
    private final EmployeeRewardRepository repository;
    private final DepartmentRewardService departmentRewardService;
    private final EmployeeService employeeService;

    public EmployeeRewardService(EmployeeRewardRepository repository, DepartmentRewardService departmentRewardService,
                                 EmployeeService employeeService) {
        this.repository = repository;
        this.departmentRewardService = departmentRewardService;
        this.employeeService = employeeService;
    }

    public EmployeeReward get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found employee reward with id=" + id));
    }

    public EmployeeReward getWithDepartment(int id) {
        return repository.findByIdWithDepartment(id).orElseThrow(() -> new NotFoundException("Not found employee reward with id=" + id));
    }

    public List<EmployeeReward> getAllByDepartmentRewardId(int departmentRewardId) {
        DepartmentReward departmentReward = departmentRewardService.get(departmentRewardId);
        return getAllByDepartmentReward(departmentReward);
    }

    public List<EmployeeReward> getAllByDepartmentReward(DepartmentReward departmentReward) {
        return repository.findAllByDepartmentRewardIdOrderByEmployeeName(departmentReward.getId());
    }

    public byte[] getAllByDepartmentRewardInPdf(DepartmentReward departmentReward, EmployeeSignature approvingSignature) {
        List<EmployeeReward> employeeRewards = repository.findAllByDepartmentRewardIdOrderByEmployeeName(departmentReward.getId());
        EmployeeSignature chiefSignature = employeeService.getChiefSignature(departmentReward.getDepartment().getId());
        return createEmployeeRewardsPdfForm(employeeRewards, departmentReward, chiefSignature, approvingSignature);
    }

    @Transactional
    public void update(EmployeeRewardTo employeeRewardTo) {
        checkToState(employeeRewardTo);
        EmployeeReward employeeReward = repository.findByIdWithDepartmentReward(employeeRewardTo.getId())
                .orElseThrow(() -> new NotFoundException("Not found employee reward with id=" + employeeRewardTo.getId()));
        DepartmentReward departmentReward = employeeReward.getDepartmentReward();
        int salary = employeeReward.getCurrentPositionSalary();
        Rate rate = employeeReward.getCurrentEmployeeRate();
        double requiredHoursWorked = departmentReward.getPaymentPeriod().getRequiredHoursWorked();

        int hoursWorkedReward = calculateHoursWorkedReward(employeeRewardTo.getHoursWorked(), salary, rate, requiredHoursWorked);
        int newFullReward = calculateFullReward(hoursWorkedReward, employeeRewardTo.getAdditionalReward(), employeeRewardTo.getPenalty(), salary, rate);
        int newDistributedAmount = calculateNewDistributedAmount(departmentReward, employeeReward.getFullReward(), newFullReward);
        updateFromTo(employeeReward, employeeRewardTo, hoursWorkedReward);
        departmentReward.setDistributedAmount(newDistributedAmount);
    }
}