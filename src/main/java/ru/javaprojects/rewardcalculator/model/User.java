package ru.javaprojects.rewardcalculator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.CollectionUtils;
import ru.javaprojects.rewardcalculator.HasManagedDepartments;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "users_unique_email_idx")})
public class User extends AbstractNamedEntity implements HasManagedDepartments {

    @Email
    @NotBlank
    @Size(max = 40)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 5, max = 100)
    // https://stackoverflow.com/a/12505165/548473
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
    private boolean enabled = true;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "registered", nullable = false, columnDefinition = "timestamp default now()")
    private Date registered = new Date();

    @NotEmpty
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "user_roles_unique_idx")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    @JoinColumn(name = "user_id") //https://stackoverflow.com/a/62848296/548473
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> roles;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    @JoinTable(
            name = "user_managed_departments",
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "department_id"}, name = "user_department_unique_idx")},
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private Set<Department> managedDepartments;

    public User() {
    }

    public User(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.isEnabled(), user.getRegistered(), user.getManagedDepartments(), user.getRoles());
    }

    public User(Integer id, String name, String email, String password, Set<Department> managedDepartments, Role role, Role... roles) {
        this(id, name, email, password, true, new Date(), managedDepartments, EnumSet.of(role, roles));
    }

    public User(Integer id, String name, String email, String password, boolean enabled, Date registered, Set<Department> managedDepartments, Collection<Role> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.registered = registered;
        this.managedDepartments = managedDepartments;
        setRoles(roles);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }

    @Override
    public Set<Department> getManagedDepartments() {
        return managedDepartments;
    }

    @Override
    public void setManagedDepartments(Set<Department> managedDepartments) {
        this.managedDepartments = managedDepartments;
    }

    public void addManagedDepartments(Department... departments) {
        if (Objects.isNull(managedDepartments)) {
            managedDepartments = new HashSet<>();
        }
        managedDepartments.addAll(Arrays.asList(departments));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", enabled=" + enabled +
                ", roles=" + roles +
                ", managedDepartments=" + managedDepartments +
                '}';
    }
}