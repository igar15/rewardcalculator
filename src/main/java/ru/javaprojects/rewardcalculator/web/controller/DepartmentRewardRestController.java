package ru.javaprojects.rewardcalculator.web.controller;

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

import static ru.javaprojects.rewardcalculator.util.SecureUtil.checkDepartmentHeadManagesTheDepartment;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = DepartmentRewardRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DepartmentRewardRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api";
    private final DepartmentRewardService service;

    public DepartmentRewardRestController(DepartmentRewardService service) {
        this.service = service;
    }

    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST", "ROLE_DEPARTMENT_HEAD"})
    @GetMapping("/departments/{departmentId}/departmentrewards")
    public List<DepartmentReward> getAll(@PathVariable int departmentId, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("getAll for department {}", departmentId);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentId);
        return service.getAllByDepartmentId(departmentId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST", "ROLE_DEPARTMENT_HEAD"})
    @GetMapping("/departments/{departmentId}/departmentrewards/byPage")
    public Page<DepartmentReward> getAll(@PathVariable int departmentId, Pageable pageable, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("getAll for department {} (pageNumber={}, pageSize={})", departmentId, pageable.getPageNumber(), pageable.getPageSize());
        checkDepartmentHeadManagesTheDepartment(authUser, departmentId);
        return service.getAllByDepartmentId(departmentId, pageable);
    }

    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST", "ROLE_DEPARTMENT_HEAD"})
    @GetMapping("/departmentrewards/{id}")
    public DepartmentReward get(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("get {}", id);
        DepartmentReward departmentReward = service.getWithDepartment(id);
        checkDepartmentHeadManagesTheDepartment(authUser, departmentReward.getDepartment());
        return departmentReward;
    }

    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @DeleteMapping("/departmentrewards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @PostMapping(value = "/departmentrewards", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentReward> createWithLocation(@Valid @RequestBody DepartmentRewardTo departmentRewardTo) {
        log.info("create {}", departmentRewardTo);
        checkNew(departmentRewardTo);
        DepartmentReward created = service.create(departmentRewardTo);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/departmentrewards/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @PutMapping(value = "/departmentrewards/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody DepartmentRewardTo departmentRewardTo, @PathVariable int id) {
        log.info("update {} with id={}", departmentRewardTo, id);
        assureIdConsistent(departmentRewardTo, id);
        service.update(departmentRewardTo);
    }
}