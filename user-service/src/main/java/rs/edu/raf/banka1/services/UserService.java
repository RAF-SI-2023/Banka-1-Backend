package rs.edu.raf.banka1.services;



import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.requests.CreateUserRequest;
import rs.edu.raf.banka1.requests.EditUserRequest;
import rs.edu.raf.banka1.requests.ModifyPermissionsRequest;
import rs.edu.raf.banka1.responses.ActivateAccountResponse;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponse findByEmail(String email);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse findByJwt();
    List<UserResponse> search(String email, String firstName, String lastName, String position);
    CreateUserResponse createUser(CreateUserRequest createUserRequest);
    ActivateAccountResponse activateAccount(String token, String password);
    boolean editUser(EditUserRequest editUserRequest);
    Boolean deleteUser(Long id);

    List<PermissionDto> findPermissions(Long userId);

    List<PermissionDto> findPermissions(String email);

    Boolean modifyUserPermissions(ModifyPermissionsRequest request, Long userId);
}
