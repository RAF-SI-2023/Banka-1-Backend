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
}
