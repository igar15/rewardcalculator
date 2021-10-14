package ru.javaprojects.rewardcalculator.to;

import ru.javaprojects.rewardcalculator.model.Role;

import javax.validation.constraints.*;
import java.util.Set;

public class UserTo extends BaseTo {

    @NotBlank
    @Size(min = 4, max = 50)
    private String name;

    @Email
    @NotBlank
    @Size(max = 40)
    private String email;

    @NotNull
    private Boolean enabled;

    @NotEmpty
    private Set<Role> roles;

    @NotNull
    private Set<Integer> managedDepartmentsId;

    public UserTo() {
    }

    public UserTo(Integer id, String name, String email, boolean enabled, Set<Role> roles, Set<Integer> managedDepartmentsId) {
        super(id);
        this.name = name;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
        this.managedDepartmentsId = managedDepartmentsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Integer> getManagedDepartmentsId() {
        return managedDepartmentsId;
    }

    public void setManagedDepartmentsId(Set<Integer> managedDepartmentsId) {
        this.managedDepartmentsId = managedDepartmentsId;
    }

    @Override
    public String toString() {
        return "UserTo{" +
                "id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", enabled=" + enabled +
                ", roles=" + roles +
                ", managedDepartmentsId=" + managedDepartmentsId +
                '}';
    }
}