package rs.edu.raf.banka1.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.requests.CreateUserRequest;
import rs.edu.raf.banka1.requests.EditUserRequest;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    private PermissionMapper permissionMapper;

    private PasswordEncoder passwordEncoder;

    public UserMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public UserResponse userToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setJmbg(user.getJmbg());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setPosition(user.getPosition());
        userResponse.setActive(user.getActive());
        userResponse.setPermissions(user.getPermissions().stream().map(permissionMapper::permissionToPermissionDto).
                collect(Collectors.toList()));
        return userResponse;
    }

    public User userResponseToUser(UserResponse userResponse) {
        User user = new User();
        user.setFirstName(userResponse.getFirstName());
        user.setLastName(userResponse.getLastName());
        user.setEmail(userResponse.getEmail());
        user.setJmbg(userResponse.getJmbg());
        user.setPhoneNumber(userResponse.getPhoneNumber());
        user.setPosition(userResponse.getPosition());
        user.setActive(userResponse.getActive());
        user.setPermissions(userResponse.getPermissions().stream().map(permissionMapper::permissionDtoToPermission).
                collect(Collectors.toSet()));
        return user;
    }

    public User createUserRequestToUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setEmail(createUserRequest.getEmail());
        user.setJmbg(createUserRequest.getJmbg());
        user.setPhoneNumber(createUserRequest.getPhoneNumber());
        user.setPosition(createUserRequest.getPosition());
        user.setActive(createUserRequest.getActive());
        user.setPassword(UUID.randomUUID().toString());
        return user;
    }

    public User editUserRequestToUser(User user, EditUserRequest editUserRequest) {
        if (editUserRequest.getPassword() != null) user.setPassword(passwordEncoder.encode(editUserRequest.getPassword()));
        if (editUserRequest.getFirstName() != null) user.setFirstName(editUserRequest.getFirstName());
        if (editUserRequest.getLastName() != null) user.setLastName(editUserRequest.getLastName());
        if (editUserRequest.getJmbg() != null) user.setJmbg(editUserRequest.getJmbg());
        if (editUserRequest.getPosition() != null) user.setPosition(editUserRequest.getPosition());
        if (editUserRequest.getPhoneNumber() != null) user.setPhoneNumber(editUserRequest.getPhoneNumber());
        if (editUserRequest.getIsActive() != null) user.setActive(editUserRequest.getIsActive());
        return user;
    }

    public EditUserRequest userToEditUserRequest(User user) {
        EditUserRequest editUserRequest = new EditUserRequest();
        editUserRequest.setFirstName(user.getFirstName());
        editUserRequest.setLastName(user.getLastName());
        editUserRequest.setEmail(user.getEmail());
        editUserRequest.setJmbg(user.getJmbg());
        editUserRequest.setPhoneNumber(user.getPhoneNumber());
        editUserRequest.setPosition(user.getPosition());
        editUserRequest.setIsActive(user.getActive());
        editUserRequest.setPermissions(user.getPermissions().stream().map(permission -> permission.getName()).collect(Collectors.toSet()));
        editUserRequest.setUserId(user.getUserId());
        editUserRequest.setPassword(user.getPassword());
        return editUserRequest;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
