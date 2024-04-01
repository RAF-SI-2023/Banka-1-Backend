package rs.edu.raf.banka1.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.requests.CreateUserRequest;
import rs.edu.raf.banka1.requests.EditUserRequest;
import rs.edu.raf.banka1.requests.ModifyPermissionsRequest;
import rs.edu.raf.banka1.responses.ActivateAccountResponse;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import rs.edu.raf.banka1.responses.NewPasswordResponse;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.UserService;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Value("${front.port}")
    private String frontPort;
    private UserMapper userMapper;
    private UserRepository userRepository;
    private PermissionRepository permissionRepository;

    private PermissionMapper permissionMapper;
    private EmailService emailService;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, EmailService emailService,
                           PermissionRepository permissionRepository,
                           JwtUtil jwtUtil, PermissionMapper permissionMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.permissionRepository = permissionRepository;
        this.jwtUtil = jwtUtil;
        this.permissionMapper = permissionMapper;
        this.passwordEncoder = passwordEncoder;
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
    public UserResponse findByJwt() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation has the email field
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            return findByEmail(email);
        }

        return null;
    }

    @Override
    public List<UserResponse> search(String email, String firstName, String lastName, String position) {
        return userRepository.searchUsersByEmailAndFirstNameAndLastNameAndPosition(email, firstName, lastName, position)
                .map(users -> users.stream().map(userMapper::userToUserResponse).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.createUserRequestToUser(createUserRequest);
        String activationToken = UUID.randomUUID().toString();
        user.setActivationToken(activationToken);
        if (createUserRequest.getPosition().equalsIgnoreCase("admin")) {
            user.setPermissions(new HashSet<>(permissionRepository.findAll()));
        }
        userRepository.save(user);
        emailService.sendEmail(createUserRequest.getEmail(), "RAF Banka - User activation",
                "Visit this URL to activate your account: http://localhost:" + frontPort + "/user/set-password/" + activationToken);
        return new CreateUserResponse(user.getUserId());
    }

    @Override
    public ActivateAccountResponse activateAccount(String token, String password) {
        User user = userRepository.findByActivationToken(token).orElseThrow();
        user.setActivationToken(null);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new ActivateAccountResponse(user.getUserId());
    }

    @Override
    public boolean editUser(EditUserRequest editUserRequest) {
        Optional<User> user = userRepository.findByEmail(editUserRequest.getEmail());
        if (user.isEmpty()) {
            return false;
        }
        User newUser = userMapper.editUserRequestToUser(user.get(), editUserRequest);
        userRepository.save(newUser);
        return true;
    }

    @Override
    public Boolean deleteUser(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getActive() != null && user.getActive()) {
                userRepository.deactivateUser(user.getUserId());
                return true;
            }
        }
        return false;
    }

    @Override
    public List<PermissionDto> findPermissions(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return null;
        }

        return extractPermissionsFromUser(user);
    }

    @Override
    public List<PermissionDto> findPermissions(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            return null;
        }

        return extractPermissionsFromUser(user);
    }

    @Override
    public Boolean modifyUserPermissions(ModifyPermissionsRequest request, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return false;
        }

        Set<Permission> permissions = user.getPermissions();
        for (String permissionName : request.getPermissions()) {
            Optional<Permission> permission = permissionRepository.findByName(permissionName);
            if(request.getAdd()) {
                permissions.add(permission.orElse(null));
            }
            else {
                permissions.remove(permission.orElse(null));
            }
        }
        userRepository.save(user);

        return true;
    }

    //necessary for authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> myUser = this.userRepository.findByEmail(username);
        if (myUser.isEmpty()) {
            throw new UsernameNotFoundException("User name " + username + " not found");
        }

        User user = myUser.get();
        //convert permissions to list of simple granted authorities used by @PreAuthorize
        List<SimpleGrantedAuthority> authorities = user.getPermissions().stream()
                .map((permission -> new SimpleGrantedAuthority(permission.getName())))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    private List<PermissionDto> extractPermissionsFromUser(User user) {
        return user.getPermissions().stream().
                map(permissionMapper::permissionToPermissionDto).collect(Collectors.toList());
    }

    @Override
    public Boolean sendResetPasswordEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) return false;
        User user = optionalUser.get();
        String resetPasswordToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetPasswordToken);
        userRepository.save(user);
        return emailService.sendEmail(email, "RAF Banka - Password reset",
                "Visit this URL to reset your password: http://localhost:" + frontPort + "/user/reset-password/" + resetPasswordToken);
    }

    @Override
    public NewPasswordResponse setNewPassword(String token, String password) {
        Optional<User> optionalUser = userRepository.findByResetPasswordToken(token);
        if (optionalUser.isEmpty()) {
            return new NewPasswordResponse();
        }
        User user = optionalUser.get();
        user.setResetPasswordToken(null);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new NewPasswordResponse(user.getUserId());
    }
}
