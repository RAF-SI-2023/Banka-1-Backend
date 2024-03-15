package rs.edu.raf.banka1.responses;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.dtos.PermissionDto;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class UserResponse {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse that = (UserResponse) o;
        return Objects.equals(userId, that.userId) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }
}

