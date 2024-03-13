package rs.edu.raf.banka1.services;



import rs.edu.raf.banka1.responses.ActivateAccountResponse;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import rs.edu.raf.banka1.responses.EditUserResponse;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserResponse findByEmail(String email);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    List<UserResponse> search(String email, String firstName, String lastName, String position);

    CreateUserResponse createUser(String email, String password, String firstName, String lastName, String jmbg, String position, String phoneNumber, boolean isActive);
    CreateUserResponse createUser(String email, String password, String firstName, String lastName, String jmbg, String position, String phoneNumber, boolean isActive, String activationToken);
    ActivateAccountResponse activateAccount(String token, String password);
    EditUserResponse editUser(String email, String password, String firstName, String lastName, String jmbg, String position, String phoneNumber, boolean isActive, Set<String> permissions);

}
