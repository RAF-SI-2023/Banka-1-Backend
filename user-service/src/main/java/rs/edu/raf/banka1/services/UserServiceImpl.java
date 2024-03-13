package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse findByEmail(String email) {
        return this.userRepository.findByEmail(email).map(userMapper::userToUserResponse).orElse(null);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponse).toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return userRepository.findById(id).map(userMapper::userToUserResponse).orElse(null);
    }

    @Override
    public List<UserResponse> search(String email, String firstName, String lastName, String position) {
        return userRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(email, firstName, lastName, position)
                .map(users -> users.stream().map(userMapper::userToUserResponse).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public CreateUserResponse createUser(String email, String firstName, String lastName, String jmbg, String position, String phoneNumber, boolean isActive) {
        return null;
    }
}
