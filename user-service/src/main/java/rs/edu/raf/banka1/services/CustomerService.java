package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.createCustomerRequest.CreateCustomerRequest;

public interface CustomerService {
    Long createNewCustomer(CreateCustomerRequest createCustomerRequest);
    boolean initialActivation(InitialActivationRequest createCustomerRequest);

    Long activateNewCustomer(String token, String password);
}
