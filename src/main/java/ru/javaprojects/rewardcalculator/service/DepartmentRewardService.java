package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.repository.DepartmentRewardRepository;
import ru.javaprojects.rewardcalculator.repository.EmployeeRewardRepository;
import ru.javaprojects.rewardcalculator.util.exception.DepartmentRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class DepartmentRewardService {
    private final DepartmentRewardRepository repository;
    private final DepartmentService departmentService;
    private final PaymentPeriodService paymentPeriodService;
    private final EmployeeService employeeService;
    private final EmployeeRewardRepository employeeRewardRepository;

    public DepartmentRewardService(DepartmentRewardRepository repository, DepartmentService departmentService,
                                   PaymentPeriodService paymentPeriodService, EmployeeService employeeService,
                                   EmployeeRewardRepository employeeRewardRepository) {
        this.repository = repository;
        this.departmentService = departmentService;
        this.paymentPeriodService = paymentPeriodService;
        this.employeeService = employeeService;
        this.employeeRewardRepository = employeeRewardRepository;
    }

    @Transactional
    public DepartmentReward create(DepartmentReward departmentReward) {
        Assert.notNull(departmentReward, "departmentReward must not be null");
        checkDistributedAMountZero(departmentReward);
        departmentService.get(departmentReward.getDepartment().id());
        paymentPeriodService.get(departmentReward.getPaymentPeriod().id());
        DepartmentReward created = repository.save(departmentReward);
        createBlankEmployeeRewards(created);
        return created;
    }

    public DepartmentReward get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found department reward with id=" + id));
    }

    public DepartmentReward getByDepartmentIdAndPaymentPeriodId(int departmentId, int paymentPeriodId) {
        departmentService.get(departmentId);
        paymentPeriodService.get(paymentPeriodId);
        return repository.findByDepartmentIdAndPaymentPeriodId(departmentId, paymentPeriodId)
                .orElseThrow(() -> new NotFoundException("Not found department reward with departmentId=" + departmentId + ", paymentPeriodId=" + paymentPeriodId));
    }
    
    public List<DepartmentReward> getAllByDepartmentId(int departmentId) {
        departmentService.get(departmentId);
        return repository.findAllByDepartmentIdOrderByPaymentPeriod_PeriodDesc(departmentId);
    }

    public void delete(int id) {
        DepartmentReward departmentReward = get(id);
        repository.delete(departmentReward);
    }

    //Update from outside using DTO
    // Do not update department
    // Do not update distributed amount
    public void update(DepartmentReward departmentReward) {
        Assert.notNull(departmentReward, "departmentReward must not be null");
        DepartmentReward dbDepartmentReward = get(departmentReward.id());
        if (dbDepartmentReward.getDistributedAmount() > departmentReward.getAllocatedAmount()) {
            throw new DepartmentRewardBadDataException("The distributed amount must be less than or equal to the allocated amount");
        }
        departmentService.get(departmentReward.getDepartment().id());
        paymentPeriodService.get(departmentReward.getPaymentPeriod().id());
        repository.save(departmentReward);
    }

    private void checkDistributedAMountZero(DepartmentReward departmentReward) {
        if (!departmentReward.getDistributedAmount().equals(0)) {
            throw new DepartmentRewardBadDataException("Distributed amount for new department reward must be 0");
        }
    }

    private void createBlankEmployeeRewards(DepartmentReward departmentReward) {
        Department department = departmentReward.getDepartment();
        List<Employee> employees = employeeService.getAllByDepartmentId(department.id());
        employees.forEach(employee ->
                employeeRewardRepository.save(new EmployeeReward(null, 0d, 0, 0, 0, employee, departmentReward)));
    }
}