package rs.edu.raf.banka1.services;



import org.springframework.security.core.userdetails.UserDetailsService;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponse findByEmail(String email);
    List<UserResponse> findAll();
}
