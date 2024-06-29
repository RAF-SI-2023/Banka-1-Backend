package rs.edu.raf.banka1.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.requests.customer.CustomerData;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.CustomerResponse;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CompanyService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    private PermissionMapper permissionMapper;

    private PasswordEncoder passwordEncoder;

    private PermissionRepository permissionRepository;
    private BankAccountMapper bankAccountMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BankAccountService bankAccountService;

    public CustomerMapper(PermissionMapper permissionMapper, BankAccountMapper bankAccountMapper) {
        this.permissionMapper = permissionMapper;
        this.bankAccountMapper = bankAccountMapper;
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
//        customer.setPosition("customer");
        customer.setPassword(UUID.randomUUID().toString());
        customer.setJmbg(createCustomerRequest.getJmbg());
        return customer;
    }

    public Customer editCustomerRequestToCustomer(Customer customer, EditCustomerRequest editCustomerRequest) {
        if(editCustomerRequest.getCompanyId() != null) {
            customer.setCompany(this.companyService.getCompanyById(editCustomerRequest.getCompanyId()));
        } else {
            customer.setCompany(null);
        }
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
//        if (editCustomerRequest.getPosition() != null) {
//            customer.setPosition(editCustomerRequest.getPosition());
//        }
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

    public CustomerResponse customerToCustomerResponse(Customer customer) {
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setUserId(customer.getUserId());
        customerResponse.setFirstName(customer.getFirstName());
        customerResponse.setLastName(customer.getLastName());
        customerResponse.setEmail(customer.getEmail());
        customerResponse.setJmbg(customer.getJmbg());
//        customerResponse.setPosition(customer.getPosition());
        customerResponse.setPhoneNumber(customer.getPhoneNumber());
        customerResponse.setActive(customer.getActive());
        customerResponse.setPermissions(customer.getPermissions()
                .stream()
                .map(permissionMapper::permissionToPermissionDto)
                .collect(Collectors.toList())
        );

        if(customer.getCompany() != null) {
            customerResponse.setIsLegalEntity(true);
            customerResponse.setCompany(customer.getCompany().getCompanyName());
        } else {
            customerResponse.setIsLegalEntity(false);
            customerResponse.setCompany("No company");
        }


        customerResponse.setDateOfBirth(customer.getDateOfBirth());
        customerResponse.setGender(customer.getGender());
        customerResponse.setAddress(customer.getAddress());

        List<BankAccountDto> accountDtoList = customer
                .getAccountIds()
                .stream()
                .map(bankAccountMapper::toDto)
                .collect(Collectors.toList());

        if(customer.getCompany() != null) {
            List<BankAccount> companyAccounts = bankAccountService.getBankAccountsByCompany(customer.getCompany().getId());
            accountDtoList.addAll(companyAccounts.stream().map(bankAccountMapper::toDto).toList());
        }

        customerResponse.setAccountIds(accountDtoList);

        return customerResponse;
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
