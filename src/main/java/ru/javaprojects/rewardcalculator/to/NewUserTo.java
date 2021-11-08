package ru.javaprojects.rewardcalculator.to;

import ru.javaprojects.rewardcalculator.model.Role;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class NewUserTo extends UserTo {

    @NotNull
    private Boolean enabled;

    @NotBlank
    @Size(min = 5, max = 32)
    private String password;

    public NewUserTo() {
    }

    public NewUserTo(Integer id, String name, String email, String password, boolean enabled, Set<Role> roles, Set<Integer> managedDepartmentsId) {
        super(id, name, email, roles, managedDepartmentsId);
        this.enabled = enabled;
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}