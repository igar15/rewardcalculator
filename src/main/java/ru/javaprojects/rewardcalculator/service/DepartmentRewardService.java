package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.repository.DepartmentRewardRepository;
import ru.javaprojects.rewardcalculator.util.exception.DepartmentRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class DepartmentRewardService {
    private final DepartmentRewardRepository repository;
    private final DepartmentService departmentService;
    private final PaymentPeriodService paymentPeriodService;

    public DepartmentRewardService(DepartmentRewardRepository repository, DepartmentService departmentService, PaymentPeriodService paymentPeriodService) {
        this.repository = repository;
        this.departmentService = departmentService;
        this.paymentPeriodService = paymentPeriodService;
    }

    public DepartmentReward create(DepartmentReward departmentReward) {
        Assert.notNull(departmentReward, "departmentReward must not be null");
        if (!isDistributedAMountEqualsZero(departmentReward)) {
            throw new DepartmentRewardBadDataException("Distributed amount for new department reward must be 0");
        }
        departmentService.get(departmentReward.getDepartment().id());
        paymentPeriodService.get(departmentReward.getPaymentPeriod().id());
        return repository.save(departmentReward);
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

    private boolean isDistributedAMountEqualsZero(DepartmentReward departmentReward) {
        return departmentReward.getDistributedAmount().equals(0);
    }
}