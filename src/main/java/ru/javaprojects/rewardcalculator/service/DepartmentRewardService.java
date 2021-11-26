package ru.javaprojects.rewardcalculator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.*;
import ru.javaprojects.rewardcalculator.repository.DepartmentRewardRepository;
import ru.javaprojects.rewardcalculator.repository.EmployeeRepository;
import ru.javaprojects.rewardcalculator.repository.EmployeeRewardRepository;
import ru.javaprojects.rewardcalculator.to.DepartmentRewardTo;
import ru.javaprojects.rewardcalculator.util.exception.DepartmentRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

import static ru.javaprojects.rewardcalculator.util.DepartmentRewardUtil.createFromTo;
import static ru.javaprojects.rewardcalculator.util.DepartmentRewardUtil.updateFromTo;

@Service
public class DepartmentRewardService {
    private final DepartmentRewardRepository repository;
    private final DepartmentService departmentService;
    private final PaymentPeriodService paymentPeriodService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRewardRepository employeeRewardRepository;

    public DepartmentRewardService(DepartmentRewardRepository repository, DepartmentService departmentService,
                                   PaymentPeriodService paymentPeriodService, EmployeeRepository employeeRepository,
                                   EmployeeRewardRepository employeeRewardRepository) {
        this.repository = repository;
        this.departmentService = departmentService;
        this.paymentPeriodService = paymentPeriodService;
        this.employeeRepository = employeeRepository;
        this.employeeRewardRepository = employeeRewardRepository;
    }

    @Transactional
    public DepartmentReward create(DepartmentRewardTo departmentRewardTo) {
        Assert.notNull(departmentRewardTo, "departmentRewardTo must not be null");
        Department department = departmentService.get(departmentRewardTo.getDepartmentId());
        PaymentPeriod paymentPeriod = paymentPeriodService.get(departmentRewardTo.getPaymentPeriodId());
        DepartmentReward departmentReward = createFromTo(departmentRewardTo);
        departmentReward.setDepartment(department);
        departmentReward.setPaymentPeriod(paymentPeriod);
        DepartmentReward created = repository.save(departmentReward);
        createBlankEmployeeRewards(created);
        return created;
    }

    public DepartmentReward get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found department reward with id=" + id));
    }

    public DepartmentReward getWithDepartment(int id) {
        return repository.findByIdWithDepartment(id).orElseThrow(() -> new NotFoundException("Not found department reward with id=" + id));
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

    public Page<DepartmentReward> getAllByDepartmentId(int departmentId, Pageable pageable) {
        Assert.notNull(pageable, "pageable must not be null");
        departmentService.get(departmentId);
        return repository.findAllByDepartmentIdOrderByPaymentPeriod_PeriodDesc(departmentId, pageable);
    }

    public void delete(int id) {
        DepartmentReward departmentReward = get(id);
        repository.delete(departmentReward);
    }

    @Transactional
    public void update(DepartmentRewardTo departmentRewardTo) {
        Assert.notNull(departmentRewardTo, "departmentRewardTo must not be null");
        DepartmentReward departmentReward = get(departmentRewardTo.getId());
        if (departmentReward.getDistributedAmount() > departmentRewardTo.getAllocatedAmount()) {
            throw new DepartmentRewardBadDataException("The allocated amount must be greater than or equal to the distributed amount");
        }
        updateFromTo(departmentReward, departmentRewardTo);
    }

    private void createBlankEmployeeRewards(DepartmentReward departmentReward) {
        Department department = departmentReward.getDepartment();
        List<Employee> employees = employeeRepository.findAllByPositionDepartmentIdAndFired(department.id(), false);
        employees.forEach(employee ->
                employeeRewardRepository.save(new EmployeeReward(null, 0d, 0, 0, 0, employee, departmentReward)));
    }
}