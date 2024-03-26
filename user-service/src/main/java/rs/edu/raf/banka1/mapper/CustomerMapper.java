package rs.edu.raf.banka1.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.requests.customer.CustomerData;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    private PermissionMapper permissionMapper;

    private PasswordEncoder passwordEncoder;

    private PermissionRepository permissionRepository;

    public CustomerMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public static Customer customerDataToCustomer(CustomerData createCustomerRequest){
        Customer customer = new Customer();
        customer.setFirstName(createCustomerRequest.getFirstName());
        customer.setLastName(createCustomerRequest.getLastName());
        customer.setDateOfBirth(createCustomerRequest.getDateOfBirth());
        customer.setGender(createCustomerRequest.getGender());
        customer.setEmail(createCustomerRequest.getEmail());
        customer.setPhoneNumber(createCustomerRequest.getPhoneNumber());
        customer.setAddress(createCustomerRequest.getAddress());
        customer.setActive(false);
        customer.setPassword(UUID.randomUUID().toString());
        customer.setJmbg(createCustomerRequest.getJmbg());
        return customer;
    }

    public Customer editCustomerRequestToCustomer(Customer customer, EditCustomerRequest editCustomerRequest) {
        if (editCustomerRequest.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(editCustomerRequest.getPassword()));
        }
        if (editCustomerRequest.getFirstName() != null) {
            customer.setFirstName(editCustomerRequest.getFirstName());
        }
        if (editCustomerRequest.getLastName() != null) {
            customer.setLastName(editCustomerRequest.getLastName());
        }
        if (editCustomerRequest.getGender() != null) {
            customer.setGender(editCustomerRequest.getGender());
        }
        if (editCustomerRequest.getAddress() != null) {
            customer.setAddress(editCustomerRequest.getAddress());
        }
        if (editCustomerRequest.getPosition() != null) {
            customer.setPosition(editCustomerRequest.getPosition());
        }
        if (editCustomerRequest.getPhoneNumber() != null) {
            customer.setPhoneNumber(editCustomerRequest.getPhoneNumber());
        }
        if (editCustomerRequest.getIsActive() != null) {
            customer.setActive(editCustomerRequest.getIsActive());
        }
        if (editCustomerRequest.getPermissions() != null) {
            customer.setPermissions(editCustomerRequest.getPermissions()
                    .stream()
                    .map(permissionString -> permissionRepository.findByName(permissionString).orElseThrow())
                    .collect(Collectors.toSet())
            );
        }
        return customer;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setPermissionRepository(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
}
