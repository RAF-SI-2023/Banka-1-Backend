package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse findByUsername(String username) {
        return this.userRepository.findByUsername(username).map(userMapper::userToUserResponse).orElse(null);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponse).toList();
    }
}
