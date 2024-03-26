package rs.edu.raf.banka1.mapper;

import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.requests.createCustomerRequest.CustomerData;

import java.util.UUID;

public class CustomerMapper {
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
}
