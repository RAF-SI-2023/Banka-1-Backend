package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.CustomerResponse;

import java.util.List;

public interface CustomerService {
    Long createNewCustomer(CreateCustomerRequest createCustomerRequest);
    boolean initialActivation(InitialActivationRequest createCustomerRequest);

    Long activateNewCustomer(String token, String password);

    List<CustomerResponse> findAll();

    boolean editCustomer(EditCustomerRequest editCustomerRequest);
}
