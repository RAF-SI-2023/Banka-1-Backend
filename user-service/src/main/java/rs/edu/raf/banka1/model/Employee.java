package rs.edu.raf.banka1.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Employee extends User {

    @Column
    private String position;

    // Agent fields
    @Column
    private Double limitNow = 0.0;

    @Column
    private Double orderlimit;

    @Column
    private Boolean requireApproval;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permissions_employee",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}
