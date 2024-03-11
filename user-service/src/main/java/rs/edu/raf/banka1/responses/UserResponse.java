package rs.edu.raf.banka1.responses;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.Permission;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class UserResponse {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String jmbg;
    private String position;
    private String phoneNumber;
    private Boolean active;
    private Set<Permission> permissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse that = (UserResponse) o;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(jmbg, that.jmbg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, jmbg);
    }
}

