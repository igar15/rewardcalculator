package ru.javaprojects.rewardcalculator.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.service.DepartmentRewardService;
import ru.javaprojects.rewardcalculator.service.EmployeeRewardService;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.web.security.AuthorizedUser;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.ALLOWED_ADMIN_DEPARTMENT_HEAD;
import static ru.javaprojects.rewardcalculator.util.EmployeeRewardUtil.EmployeeSignature;
import static ru.javaprojects.rewardcalculator.util.SecureUtil.checkDepartmentHeadManagesTheDepartment;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(value = EmployeeRewardRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Secured({"ROLE_ADMIN", "ROLE_DEPARTMENT_HEAD"})
@Tag(name = "Employee Reward Controller")
public class EmployeeRewardRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api";
    private final EmployeeRewardService service;
    private final DepartmentRewardService departmentRewardService;

    public EmployeeRewardRestController(EmployeeRewardService service, DepartmentRewardService departmentRewardService) {
        this.service = service;
        this.departmentRewardService = departmentRewardService;
    }

    @GetMapping("/departmentrewards/{departmentRewardId}/employeerewards")
    @Operation(description = "Get all employee rewards of the department reward" + ALLOWED_ADMIN_DEPARTMENT_HEAD)
    public List<EmployeeReward> getAll(@PathVariable int departmentRewardId, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("getAll for departmentReward {}", departmentRewardId);
        DepartmentReward departmentReward = departmentRewardService.getWithDepartment(departmentRewardId);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentReward.getDepartment());
        return service.getAllByDepartmentReward(departmentReward);
    }

    @GetMapping("/employeerewards/{id}")
    @Operation(description = "Get employee reward" + ALLOWED_ADMIN_DEPARTMENT_HEAD)
    public EmployeeReward get(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("get {}", id);
        return checkDepartmentHeadManagesTheEmployeeReward(id, authUser);
    }

    @PutMapping(value = "/employeerewards/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Update employee reward" + ALLOWED_ADMIN_DEPARTMENT_HEAD)
    public void update(@Valid @RequestBody EmployeeRewardTo employeeRewardTo, @PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("update {} with id={}", employeeRewardTo, id);
        assureIdConsistent(employeeRewardTo, id);
        checkDepartmentHeadManagesTheEmployeeReward(employeeRewardTo.getId(), authUser);
        service.update(employeeRewardTo);
    }

    @GetMapping(value = "/departmentrewards/{departmentRewardId}/employeerewards/pdf",
                produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(description = "Get all employee rewards of the department reward in PDF file" + ALLOWED_ADMIN_DEPARTMENT_HEAD)
    public @ResponseBody byte[] getAllInPdf(@PathVariable int departmentRewardId,
                                            @AuthenticationPrincipal AuthorizedUser authUser,
                                            @RequestParam(required = false) String approvingPosition,
                                            @RequestParam(required = false) String approvingName) {
        log.info("getAll for departmentReward {} in pdf", departmentRewardId);
        DepartmentReward departmentReward = departmentRewardService.getWithDepartment(departmentRewardId);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentReward.getDepartment());
        approvingPosition = Objects.isNull(approvingPosition) ? "" : approvingPosition;
        approvingName = Objects.isNull(approvingName) ? "" : approvingName;
        return service.getAllByDepartmentRewardInPdf(departmentReward, new EmployeeSignature(approvingPosition, approvingName));
    }

    private EmployeeReward checkDepartmentHeadManagesTheEmployeeReward(int employeeRewardId, AuthorizedUser authUser) {
        EmployeeReward employeeReward = service.getWithDepartment(employeeRewardId);
        checkDepartmentHeadManagesTheDepartment(authUser, employeeReward.getDepartmentReward().getDepartment());
        return employeeReward;
    }
}