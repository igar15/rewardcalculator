package ru.javaprojects.rewardcalculator.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.service.DepartmentRewardService;
import ru.javaprojects.rewardcalculator.to.DepartmentRewardTo;
import ru.javaprojects.rewardcalculator.web.security.AuthorizedUser;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.ALLOWED_ADMIN_ECONOMIST;
import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.ALLOWED_ADMIN_ECONOMIST_DEPARTMENT_HEAD;
import static ru.javaprojects.rewardcalculator.util.SecureUtil.checkDepartmentHeadManagesTheDepartment;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = DepartmentRewardRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Department Reward Controller")
public class DepartmentRewardRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api";
    private final DepartmentRewardService service;

    public DepartmentRewardRestController(DepartmentRewardService service) {
        this.service = service;
    }

    @GetMapping("/departments/{departmentId}/departmentrewards")
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST", "ROLE_DEPARTMENT_HEAD"})
    @Operation(description = "Get all department rewards of the department" + ALLOWED_ADMIN_ECONOMIST_DEPARTMENT_HEAD)
    public List<DepartmentReward> getAll(@PathVariable int departmentId, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("getAll for department {}", departmentId);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentId);
        return service.getAllByDepartmentId(departmentId);
    }

    @GetMapping("/departments/{departmentId}/departmentrewards/byPage")
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST", "ROLE_DEPARTMENT_HEAD"})
    @Operation(description = "Get page of department rewards of the department" + ALLOWED_ADMIN_ECONOMIST_DEPARTMENT_HEAD)
    public Page<DepartmentReward> getAll(@PathVariable int departmentId, Pageable pageable, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("getAll for department {} (pageNumber={}, pageSize={})", departmentId, pageable.getPageNumber(), pageable.getPageSize());
        checkDepartmentHeadManagesTheDepartment(authUser, departmentId);
        return service.getAllByDepartmentId(departmentId, pageable);
    }

    @GetMapping("/departmentrewards/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST", "ROLE_DEPARTMENT_HEAD"})
    @Operation(description = "Get department reward" + ALLOWED_ADMIN_ECONOMIST_DEPARTMENT_HEAD)
    public DepartmentReward get(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("get {}", id);
        DepartmentReward departmentReward = service.getWithDepartment(id);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentReward.getDepartment());
        return departmentReward;
    }

    @DeleteMapping("/departmentrewards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @Operation(description = "Delete department reward" + ALLOWED_ADMIN_ECONOMIST)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PostMapping(value = "/departmentrewards", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @Operation(description = "Create new department reward" + ALLOWED_ADMIN_ECONOMIST)
    public ResponseEntity<DepartmentReward> createWithLocation(@Valid @RequestBody DepartmentRewardTo departmentRewardTo) {
        log.info("create {}", departmentRewardTo);
        checkNew(departmentRewardTo);
        DepartmentReward created = service.create(departmentRewardTo);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/departmentrewards/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/departmentrewards/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @Operation(description = "Update department reward" + ALLOWED_ADMIN_ECONOMIST)
    public void update(@Valid @RequestBody DepartmentRewardTo departmentRewardTo, @PathVariable int id) {
        log.info("update {} with id={}", departmentRewardTo, id);
        assureIdConsistent(departmentRewardTo, id);
        service.update(departmentRewardTo);
    }
}