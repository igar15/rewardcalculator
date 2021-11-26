package ru.javaprojects.rewardcalculator.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.service.EmployeeService;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;
import ru.javaprojects.rewardcalculator.web.security.AuthorizedUser;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.ALLOWED_ADMIN;
import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.ALLOWED_ADMIN_PERSONNEL_OFFICER;
import static ru.javaprojects.rewardcalculator.util.SecureUtil.checkDepartmentHeadManagesTheDepartment;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = EmployeeRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Employee Controller")
public class EmployeeRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api";
    private final EmployeeService service;

    public EmployeeRestController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/departments/{departmentId}/employees")
    @Operation(description = "Get all not fired employees of the department")
    public List<Employee> getAllNotFired(@PathVariable int departmentId, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("getAllNotFired for department {}", departmentId);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentId);
        return service.getAllNotFiredByDepartmentId(departmentId);
    }

    @GetMapping("/departments/{departmentId}/employees/fired")
    @Operation(description = "Get all fired employees of the department")
    public List<Employee> getAllFired(@PathVariable int departmentId, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("getAllFired for department {}", departmentId);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentId);
        return service.getAllFiredByDepartmentId(departmentId);
    }

    @GetMapping("/employees/{id}")
    @Operation(description = "Get employee")
    public Employee get(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("get {}", id);
        Employee employee = service.getWithPositionDepartment(id);
        checkDepartmentHeadManagesTheDepartment(authUser, employee.getPosition().getDepartment());
        return employee;
    }

    @DeleteMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_PERSONNEL_OFFICER"})
    @Operation(description = "Delete employee" + ALLOWED_ADMIN_PERSONNEL_OFFICER)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PostMapping(value = "/employees", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_PERSONNEL_OFFICER"})
    @Operation(description = "Create new employee" + ALLOWED_ADMIN_PERSONNEL_OFFICER)
    public ResponseEntity<Employee> createWithLocation(@Valid @RequestBody EmployeeTo employeeTo) {
        log.info("create {}", employeeTo);
        checkNew(employeeTo);
        Employee created = service.create(employeeTo);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/employees/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/employees/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_PERSONNEL_OFFICER"})
    @Operation(description = "Update employee" + ALLOWED_ADMIN_PERSONNEL_OFFICER)
    public void update(@Valid @RequestBody EmployeeTo employeeTo, @PathVariable int id) {
        log.info("update {} with id={}", employeeTo, id);
        assureIdConsistent(employeeTo, id);
        service.update(employeeTo);
    }

    @PatchMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_PERSONNEL_OFFICER"})
    @Operation(description = "Change employee's working status (fired/not fired)" + ALLOWED_ADMIN_PERSONNEL_OFFICER)
    public void changeWorkingStatus(@PathVariable int id, @RequestParam boolean fired) {
        log.info(fired ? "fire {}" : "recruit {}", id);
        service.changeWorkingStatus(id, fired);
    }
}