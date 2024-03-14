package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.requests.CreateUserRequest;
import rs.edu.raf.banka1.requests.EditUserRequest;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse userToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setJmbg(user.getJmbg());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setPosition(user.getPosition());
        userResponse.setActive(user.getActive());
        userResponse.setPermissions(user.getPermissions());
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
        user.setPermissions(userResponse.getPermissions());
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
        user.setActive(createUserRequest.isActive());
        return user;
    }

    public User editUserRequestToUser(User user, EditUserRequest editUserRequest) {
        user.setPassword(editUserRequest.getPassword());
        user.setFirstName(editUserRequest.getFirstName());
        user.setLastName(editUserRequest.getLastName());
        user.setJmbg(editUserRequest.getJmbg());
        user.setPosition(editUserRequest.getPosition());
        user.setPhoneNumber(editUserRequest.getPhoneNumber());
        user.setActive(editUserRequest.isActive());
        return user;
    }
}
