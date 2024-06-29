package rs.edu.raf.banka1.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.model.Permission;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class CustomerResponse {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String jmbg;
    private String phoneNumber;
    private Boolean active;
    private List<PermissionDto  > permissions;

    private Long dateOfBirth;
    private String gender;
    private String address;
    private List<BankAccountDto> accountIds;
    private Boolean isLegalEntity;
    private String company;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerResponse that = (CustomerResponse) o;
        return Objects.equals(userId, that.userId) && Objects.equals(email, that.email) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(jmbg, that.jmbg) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, firstName, lastName, jmbg, phoneNumber);
    }
}
