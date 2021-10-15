package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.repository.EmployeeRepository;
import ru.javaprojects.rewardcalculator.service.EmployeeService;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.DepartmentTestData.DEPARTMENT_1_ID;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.getNew;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.getNewTo;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.*;
import static ru.javaprojects.rewardcalculator.PositionTestData.*;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.VALIDATION_ERROR;

class EmployeeRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = EmployeeRestController.REST_URL + '/';

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeRepository repository;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1, employee2, employee3));
    }

    @Test
    void getAllWithNotExistedDepartment() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + NOT_FOUND + "/employees"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + EMPLOYEE_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + EMPLOYEE_3_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(EMPLOYEE_3_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createWithLocation() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isCreated());

        Employee created = readFromJson(action, Employee.class);
        int newId = created.id();
        Employee newEmployee = getNew();
        newEmployee.setId(newId);
        EMPLOYEE_MATCHER.assertMatch(created, newEmployee);
        EMPLOYEE_MATCHER.assertMatch(service.get(newId), newEmployee);
    }

    @Test
    void createWithNotExistedPosition() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        newEmployeeTo.setPositionId(NOT_FOUND);
        perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void update() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        EMPLOYEE_MATCHER.assertMatch(service.get(EMPLOYEE_1_ID), getUpdated());
    }

    @Test
    void updateWithNotExistedPosition() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateChangePosition() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(POSITION_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        Employee employee = repository.findByIdWithPosition(EMPLOYEE_1_ID);
        EMPLOYEE_MATCHER.assertMatch(employee, getUpdated());
        POSITION_MATCHER.assertMatch(employee.getPosition(), position2);
    }

    @Test
    void updateNotFound() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createInvalid() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        newEmployeeTo.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateInvalid() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "/employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }
}