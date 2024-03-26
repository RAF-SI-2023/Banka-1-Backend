package rs.edu.raf.banka1.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId")
public class Customer extends User {
    //    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long userId;
//
//    @Column(unique = true)
//    @NotBlank(message = "Email is mandatory")
//    private String email;
//
//    @Column
//    @NotBlank(message = "Password is mandatory")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private String password;
//
//    @Column
//    private String firstName;
//
//    @Column
//    private String lastName;
//
//    @Column(unique = true)
//    private String jmbg;
//
    @Column
    private Long dateOfBirth;
    //
    @Column
    private String gender;
    //
    @Column
    private String address;
    //
//    @Column
//    private String phoneNumber;
//
//    @Column
//    private Boolean active;
//
//    @Column
//    private String activationToken;
    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BankAccount> accountIds;

}