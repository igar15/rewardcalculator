package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.repository.EmployeeRewardRepository;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class EmployeeRewardService {
    private final EmployeeRewardRepository repository;
    private final EmployeeService employeeService;
    private final DepartmentRewardService departmentRewardService;

    public EmployeeRewardService(EmployeeRewardRepository repository, EmployeeService employeeService, DepartmentRewardService departmentRewardService) {
        this.repository = repository;
        this.employeeService = employeeService;
        this.departmentRewardService = departmentRewardService;
    }

    public EmployeeReward get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found employee reward with id=" + id));
    }

    public List<EmployeeReward> getAllByDepartmentRewardId(int departmentRewardId) {
        departmentRewardService.get(departmentRewardId);
        return repository.findAllByDepartmentRewardIdOrderByEmployeeName(departmentRewardId);
    }

    //do not update employee and departmentReward
    @Transactional
    public EmployeeReward update(EmployeeReward employeeReward) {
        Assert.notNull(employeeReward, "employeeReward must not be null");
        EmployeeReward dbEmployeeReward = get(employeeReward.id());
        checkEmployeeRewardNegative(employeeReward);
        employeeService.get(employeeReward.getEmployee().id());
        DepartmentReward departmentReward = departmentRewardService.get(employeeReward.getDepartmentReward().id());
        checkAllocatedAmountExceeded(departmentReward, employeeReward, dbEmployeeReward);
        departmentReward.setDistributedAmount(recalculateDistributedAmount(departmentReward, employeeReward, dbEmployeeReward));
        departmentRewardService.update(departmentReward);
        return repository.save(employeeReward);
    }

    private void checkAllocatedAmountExceeded(DepartmentReward departmentReward, EmployeeReward employeeReward, EmployeeReward dbEmployeeReward) {
        if (recalculateDistributedAmount(departmentReward, employeeReward, dbEmployeeReward) > departmentReward.getAllocatedAmount()) {
            throw new EmployeeRewardBadDataException("Department reward allocated amount exceeded");
        }
    }


    private void checkEmployeeRewardNegative(EmployeeReward employeeReward) {
        if (employeeReward.getFullReward() < 0) {
            throw new EmployeeRewardBadDataException("Employee reward must be greater than or equal zero");
        }
    }

    private int recalculateDistributedAmount(DepartmentReward departmentReward, EmployeeReward employeeReward, EmployeeReward dbEmployeeReward) {
        return departmentReward.getDistributedAmount() - dbEmployeeReward.getFullReward() + employeeReward.getFullReward();
    }
}