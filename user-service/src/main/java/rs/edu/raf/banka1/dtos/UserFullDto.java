package rs.edu.raf.banka1.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserFullDto {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String jmbg;
    private String position;
    private String phoneNumber;
    private Boolean active;
    private List<PermissionDto> permissions;
}

