package rs.edu.raf.banka1.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.CustomerResponse;
import rs.edu.raf.banka1.responses.NewPasswordResponse;

import java.util.List;

public interface CustomerService extends UserDetailsService {
    Long createNewCustomer(CreateCustomerRequest createCustomerRequest);
    boolean initialActivation(InitialActivationRequest createCustomerRequest);
    CustomerResponse findByJwt();
    CustomerResponse findByEmail(String email);

    Long activateNewCustomer(String token, String password);

    List<CustomerResponse> findAll();

    boolean editCustomer(EditCustomerRequest editCustomerRequest);
    Boolean sendResetPasswordEmail(String email);
    NewPasswordResponse setNewPassword(String token, String password);
}
