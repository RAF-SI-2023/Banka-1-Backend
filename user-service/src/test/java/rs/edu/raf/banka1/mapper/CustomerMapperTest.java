package rs.edu.raf.banka1.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.requests.customer.CustomerData;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.CustomerResponse;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CustomerMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomerMapper customerMapper = new CustomerMapper(new PermissionMapper(), new BankAccountMapper());

    @BeforeEach
    void setUp() {
        customerMapper.setPasswordEncoder(passwordEncoder);
    }
    @Test
    public void editCustomerRequestToCustomer(){
        EditCustomerRequest editCustomerRequest = new EditCustomerRequest();
        editCustomerRequest.setFirstName("firstName");
        editCustomerRequest.setLastName("lastName");
        editCustomerRequest.setPassword("password");
        editCustomerRequest.setGender("M");
        editCustomerRequest.setAddress("address");
        editCustomerRequest.setPhoneNumber("phoneNumber");
        editCustomerRequest.setIsActive(true);
        editCustomerRequest.setPermissions(new ArrayList<>());

        when(passwordEncoder.encode(editCustomerRequest.getPassword())).thenReturn("password");

        Customer customer = customerMapper.editCustomerRequestToCustomer(new Customer(), editCustomerRequest);

        assertEquals(editCustomerRequest.getFirstName(), customer.getFirstName());
        assertEquals(editCustomerRequest.getLastName(), customer.getLastName());
        assertEquals(editCustomerRequest.getGender(), customer.getGender());
        assertEquals(editCustomerRequest.getAddress(), customer.getAddress());
        assertEquals(editCustomerRequest.getPhoneNumber(), customer.getPhoneNumber());
        assertEquals(editCustomerRequest.getIsActive(), true);
    }

    @Test
    public void customerToCustomerResponse(){
        Customer customer = new Customer();
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        customer.setGender("M");
        customer.setAddress("address");
        customer.setPhoneNumber("phoneNumber");
        customer.setActive(true);
        customer.setAccountIds(new ArrayList<>());

        CustomerResponse customerData = customerMapper.customerToCustomerResponse(customer);

        assertEquals(customer.getFirstName(), customerData.getFirstName());
        assertEquals(customer.getLastName(), customerData.getLastName());
        assertEquals(customer.getGender(), customerData.getGender());
        assertEquals(customer.getAddress(), customerData.getAddress());
        assertEquals(customer.getPhoneNumber(), customerData.getPhoneNumber());
        assertEquals(customer.getActive(), true);
    }

}