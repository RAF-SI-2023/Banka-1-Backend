package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.responses.UserResponse;

@Component
public class UserMapper {

    public UserResponse userToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        return userResponse;
    }

    public User userResponseToUser(UserResponse userResponse) {
        User user = new User();
        user.setUsername(userResponse.getUsername());
        user.setFirstName(userResponse.getFirstName());
        user.setLastName(userResponse.getLastName());
        user.setEmail(userResponse.getEmail());
        return user;
    }
}
