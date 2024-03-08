package rs.edu.raf.banka1.services;


import rs.edu.raf.banka1.responses.UserResponse;

import java.util.List;

public interface UserService {
    public UserResponse findByUsername(String username);
    public List<UserResponse> findAll();
}
