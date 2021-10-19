package ru.javaprojects.rewardcalculator.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.service.EmployeeRewardService;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;

import javax.validation.Valid;
import java.util.List;

import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(value = EmployeeRewardRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeeRewardRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api";
    private final EmployeeRewardService service;

    public EmployeeRewardRestController(EmployeeRewardService service) {
        this.service = service;
    }

    @GetMapping("/departmentrewards/{departmentRewardId}/employeerewards")
    public List<EmployeeReward> getAll(@PathVariable int departmentRewardId) {
        log.info("getAll for departmentReward {}", departmentRewardId);
        return service.getAllByDepartmentRewardId(departmentRewardId);
    }

    @GetMapping("/employeerewards/{id}")
    public EmployeeReward get(@PathVariable int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @PutMapping(value = "/employeerewards/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody EmployeeRewardTo employeeRewardTo, @PathVariable int id) {
        log.info("update {} with id={}", employeeRewardTo, id);
        assureIdConsistent(employeeRewardTo, id);
        service.update(employeeRewardTo);
    }
}