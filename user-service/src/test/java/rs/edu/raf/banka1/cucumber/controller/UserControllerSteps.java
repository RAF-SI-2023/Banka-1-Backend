package rs.edu.raf.banka1.cucumber.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.cucumber.SpringIntegrationTest;
//import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.dtos.*;
import rs.edu.raf.banka1.dtos.employee.CreateEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EditEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.mapper.*;
import rs.edu.raf.banka1.model.*;
//import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.repositories.*;
//import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.requests.*;
import rs.edu.raf.banka1.requests.customer.AccountData;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.CustomerData;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.responses.*;
import rs.edu.raf.banka1.services.EmailService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerSteps {

    private PasswordEncoder passwordEncoder = null;

    @Autowired
    private EmailService emailService;
    private String port = Integer.toString(SpringIntegrationTest.enviroment.getServicePort("user-service", 8080));

    private EmployeeRepository userRepository;
    //    private ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
//    private ForeignCurrencyAccountRequest foreignCurrencyAccountRequest = new ForeignCurrencyAccountRequest();
    private PermissionRepository permissionRepository;
    private PaymentRepository paymentRepository;
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private PaymentRecipientRepository paymentRecipientRepository;
    private TransferRepository transferRepository;
    private LoanRequestRepository loanRequestRepository;
    private EmployeeRepository employeeRepository;
    private CardRepository cardRepository;
    private LoanRepository loanRepository;



    private String jwt = "";

    private EmployeeDto lastReadUserResponse;
    private List<EmployeeDto> lastReadAllUsersResponse;
    private List<CustomerResponse> lastReadAllCustomersResponse;
    private CreateUserResponse lastCreateUserResponse;
//    private CreateForeignCurrencyAccountResponse lastCreateForeignCurrencyAccountResponse;
//    private List<ForeignCurrencyAccountResponse> lastReadAllForeignCurrencyAccountsResponse;
    private EditUserResponse lastEditUserResponse;
    private ActivateAccountResponse lastActivateAccountResponse;
    private User activatedUser;
    private CustomerResponse editUserRequest = new CustomerResponse();
    private CreateEmployeeDto createUserRequest = new CreateEmployeeDto();
    private Long userToRemove;
    private String email;
    private ResponseEntity<?> lastResponse;

    private EmployeeMapper userMapper = new EmployeeMapper(new PermissionMapper(), passwordEncoder, permissionRepository);
    private CustomerMapper customerMapper = new CustomerMapper(new PermissionMapper(), new BankAccountMapper());
    private List<EmployeeDto> userResponses = new ArrayList<>();
    private List<CustomerResponse> customerResponses = new ArrayList<>();
    //private final String url = "http://localhost:";
    private final String url = "http://" + SpringIntegrationTest.enviroment.getServiceHost("user-service", 8080) + ":";
    //private final String url = "http://" + "host.docker.internal" + ":";
    private Long lastid;
    private String password;
    private String token;
    private AccountData accountData = new AccountData();
    private CustomerData customerData = new CustomerData();
    private String bankAccountNumber;
    private CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
    private Long paymentId;
    private CreatePaymentRecipientRequest createPaymentRecipientRequest = new CreatePaymentRecipientRequest();
    private PaymentRecipientDto paymentRecipientDto = new PaymentRecipientDto();
    private CreateTransferRequest createTransferRequest = new CreateTransferRequest();
    private CreateLoanRequest createLoanRequest = new CreateLoanRequest();
    private StatusRequest statusRequest = new StatusRequest();
    private CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    private BankAccountRequest bankAccountRequest = new BankAccountRequest();
    private CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
    private EditBankAccountNameRequest editBankAccountNameRequest = new EditBankAccountNameRequest();
    private EditCustomerRequest editCustomerRequest = new EditCustomerRequest();
    private CreateEmployeeDto createEmployeeDto = new CreateEmployeeDto();
    private ActivateAccountRequest activateAccountRequest = new ActivateAccountRequest();
    private NewPasswordRequest newPasswordRequest = new NewPasswordRequest();
    private EditEmployeeDto editEmployeeDto = new EditEmployeeDto();
    private ModifyPermissionsRequest modifyPermissionsRequest = new ModifyPermissionsRequest();
    private NewLimitDto newLimitDto = new NewLimitDto();

    @And("i want to add him permissions")
    public void iWantToAddHimPermissions() {
        modifyPermissionsRequest.setAdd(true);
    }

    @And("i want to remove him permissions")
    public void iWantToRemoveHimPermissions() {
        modifyPermissionsRequest.setAdd(false);
    }

    @Data
    class SearchFilter {
        private String email;
        private String firstName;
        private String lastName;
        private String position;
    }

    private SearchFilter searchFilter = new SearchFilter();

    public UserControllerSteps(EmployeeRepository userRepository,
                               PermissionRepository permissionRepository,
                               PasswordEncoder passwordEncoder,
                               CustomerRepository customerRepository,
                               BankAccountRepository bankAccountRepository,
                               PaymentRepository paymentRepository,
                               PaymentRecipientRepository paymentRecipientRepository,
                               TransferRepository transferRepository,
                               LoanRequestRepository loanRequestRepository,
                               LoanRepository loanRepository,
                               CardRepository cardRepository,
                               EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.paymentRepository = paymentRepository;
        this.paymentRecipientRepository = paymentRecipientRepository;
        this.transferRepository = transferRepository;
        this.loanRequestRepository = loanRequestRepository;
        this.loanRepository = loanRepository;
        this.cardRepository = cardRepository;
        this.employeeRepository = employeeRepository;
    }


    @Transactional
    @When("User calls get on {string}")
    public void iSendAGETRequestTo(String path) {
        userResponses.clear();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (path.equals("/employee/getAll")) {
                lastReadAllUsersResponse = objectMapper.readValue(getBody(path), new TypeReference<List<EmployeeDto>>() {
                });
                userRepository.findAll().forEach(user -> userResponses.add(userMapper.employeeToEmployeeDto(user)));
            }
            else if (path.equals("/customer/getAll")) {
                lastReadAllCustomersResponse = objectMapper.readValue(getBody(path), new TypeReference<List<CustomerResponse>>() {
                });
                customerRepository.findAll().forEach(user -> customerResponses.add(customerMapper.customerToCustomerResponse(user)));
            }
            else if (path.equals("/employee/search")) {
                lastReadAllUsersResponse = objectMapper.readValue(getFiltered(path), new TypeReference<List<EmployeeDto>>() {
                });
                userRepository.findAll().forEach(user -> {
                    if (user.getActive() == null || !user.getActive()) return;
                    if (searchFilter.getEmail() != null && !user.getEmail().equals(searchFilter.getEmail())) return;
                    if (searchFilter.getFirstName() != null && !user.getFirstName().equalsIgnoreCase(searchFilter.getFirstName()))
                        return;
                    if (searchFilter.getLastName() != null && !user.getLastName().equalsIgnoreCase(searchFilter.getLastName()))
                        return;
                    if (searchFilter.getPosition() != null
//                            &&
//                            !user.getPosition().equalsIgnoreCase(searchFilter.getPosition())
                    )
                        return;
                    userResponses.add(userMapper.employeeToEmployeeDto(user));
                });
            }
//            else if (path.startsWith("/employee/get/")) {
//                lastReadUserResponse = objectMapper.readValue(getBody(path), EmployeeDto.class);
//                String[] split = path.split("/");
//                email = split[split.length - 1];
//            }
//            else if (path.equals("/balance/foreign_currency")) {
//                lastReadAllForeignCurrencyAccountsResponse = objectMapper.readValue(getBody(path), new TypeReference<List<ForeignCurrencyAccountResponse>>() {
//                });
//            }
//            else if (path.startsWith("/employee/")) {
//                lastReadUserResponse = objectMapper.readValue(getBody(path), EmployeeDto.class);
//                String[] split = path.split("/");
//                lastid = Long.parseLong(split[split.length - 1]);
//            }
            else if(path.equals("/payment/get")){
                getBody(path + "/" + paymentId);
            }
            else if(path.equals("/transfer/")){
                getBody(path + lastid);
            }
            else if(path.equals("/employee/permissions/employeeId/id")){
                path = path.replaceAll("id", String.valueOf(lastid));
                getBody(path);
            }
            else{
                getBody(path);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @When("i send DELETE request to {string}")
    public void iSendDELETERequestTo(String path) {
        if(path.equals("/employee/remove/id")){
            path = path.replaceAll("id", String.valueOf(lastid));
            delete(path);
        }else if(path.equals("/recipients/remove/")){
            PaymentRecipient paymentRecipient = paymentRecipientRepository.findAll().stream().filter(
                recipient -> recipient.getFirstName().equals("mika")
            ).findFirst().orElse(null);

            if(paymentRecipient == null){
                fail("Recipient not found");
            }

            delete(path + paymentRecipient.getId());
        }else if(path.equals("/employee/remove/")){
            delete(path + userToRemove);
        }
    }

    @When("i send PUT request to {string}")
    public void whenISendPUTRequestTo(String path) {
        if(path.equals( "/customer")) {
            put(path, editCustomerRequest);
        }
        else if(path.equals("/recipients/edit")){
            put(path, paymentRecipientDto);
        }
        else if(path.equals("/loan/requests/")){
            put(path + lastid, statusRequest);
        }
        else if(path.equals("/account")){
            put(path, editBankAccountNameRequest);
        }
        else if(path.equals("/employee/")){
            put(path, editEmployeeDto);
        }
        else if(path.equals("/employee/limits/reset/id")){
            path = path.replaceAll("id", String.valueOf(lastid));
            putNoBody(path);
        }
        else if(path.equals("/employee/limits/newLimit")){
            newLimitDto.setUserId(lastid);
            put(path, newLimitDto);
        }
    }

    @When("user calls POST on {string}")
    public void userCallsPostOn(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (path.equals("/employee/createUser")) {
                String tmp = post(path, createUserRequest);
                lastCreateUserResponse = objectMapper.readValue(tmp, CreateUserResponse.class);
            }
            else if(path.equals("/customer/createNewCustomer")){
                CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
                createCustomerRequest.setCustomer(customerData);
                createCustomerRequest.setAccount(accountData);
                post(path, createCustomerRequest);
            }
            else if(path.equals("/customer/initialActivation")){
                InitialActivationRequest initialActivationRequest = new InitialActivationRequest();
                initialActivationRequest.setEmail(customerData.getEmail());
                initialActivationRequest.setPhoneNumber(customerData.getPhoneNumber());
                initialActivationRequest.setAccountNumber(bankAccountNumber);
                post(path, initialActivationRequest);
            }
            else if(path.equals("/customer/activate/{token}")){
                path = path.replace("{token}", token);
                ActivateAccountRequest activateAccountRequest = new ActivateAccountRequest();
                activateAccountRequest.setPassword(password);
                post(path, activateAccountRequest);
            }
            else if(path.equals("/payment/sendCode")){
                postNoBody(path);
            }
            else if(path.equals("/payment")){
                post(path, createPaymentRequest);
            }
            else if(path.equals("/recipients/add")){
                post(path, createPaymentRecipientRequest);
            }
            else if(path.equals("/transfer")){
                post(path, createTransferRequest);
            }
            else if(path.equals("/loan/requests")){
                post(path, createLoanRequest);
            }
            else if(path.equals("/orders")){
                post(path, createOrderRequest);
            }
            else if(path.equals("/account/create")){
                createBankAccountRequest.setAccount(bankAccountRequest);
                post(path, createBankAccountRequest);
            }
            else if(path.equals("/employee/activate/token")){
                path = path.replaceAll("token", token);
                post(path, activateAccountRequest);
            }
            else if(path.equals("/employee/resetPassword")){
                postNoBody(path);
            }
            else if(path.equals("/employee/newpassword/token")){
                path = path.replaceAll("token", token);
                post(path, newPasswordRequest);
            }
            else if(path.equals("/employee/createEmployee")){
                post(path, createEmployeeDto);
            }
            else if(path.equals("/employee/reset/drugizaposleni@gmail.rs")){
                postNoBody(path);
            }
            else if(path.equals("/employee/permission/employeeId")){
                path = path.replaceAll("employeeId", String.valueOf(lastid));
                post(path, modifyPermissionsRequest);
            }


//            else if (path.equals("/balance/foreign_currency/create")) {
//                lastCreateForeignCurrencyAccountResponse = objectMapper.readValue(post(path, foreignCurrencyAccountRequest), CreateForeignCurrencyAccountResponse.class);
//            }
        }
        catch (Exception e){
            e.printStackTrace();
            fail("Failed to parse response body");
        }
    }

    private HttpEntity<Object> makeRequest(Object objectToPost){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        if(objectToPost == null)
            return new HttpEntity<>(headers);
        return new HttpEntity<>(objectToPost, headers);
    }

    private String makeUrl(String path){
        return url.concat(port).concat(path);
    }

    private String getBody(String path){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(makeUrl(path), org.springframework.http.HttpMethod.GET, makeRequest(null), String.class);
        }catch (HttpClientErrorException e){
            response = new ResponseEntity<>(e.getStatusCode());
        }
        lastResponse = response;
        return response.getBody();
    }

    private String postNoBody(String path){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(makeUrl(path), org.springframework.http.HttpMethod.POST, makeRequest(null), String.class);
        }catch (HttpClientErrorException e){
            response = new ResponseEntity<>(e.getStatusCode());
        }
        lastResponse = response;
        return response.getBody();
    }

    private String post(String path, Object objectToPost){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(makeUrl(path), org.springframework.http.HttpMethod.POST, makeRequest(objectToPost), String.class);
        }catch (HttpClientErrorException e){
            response = new ResponseEntity<>(e.getStatusCode());
        }
        lastResponse = response;
        return response.getBody();

    }

    private String getFiltered(String path){
        char combiner = '?';
        if(searchFilter.getEmail() != null) {
            path = path.concat(combiner + "email=" + searchFilter.getEmail());
            combiner = '&';
        }
        if(searchFilter.getFirstName() != null) {
            path = path.concat(combiner + "firstName=" + searchFilter.getFirstName());
            combiner = '&';
        }
        if(searchFilter.getLastName() != null) {
            path = path.concat(combiner + "lastName=" + searchFilter.getLastName());
            combiner = '&';
        }
        if(searchFilter.getPosition() != null) {
            path = path.concat(combiner + "position=" + searchFilter.getPosition());
        }

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(makeUrl(path), org.springframework.http.HttpMethod.GET, makeRequest(null), String.class);
        }catch (HttpClientErrorException e){
            response = new ResponseEntity<>(e.getStatusCode());
        }
        lastResponse = response;
        return response.getBody();
    }


    private void put(String path, Object objectToPut){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(makeUrl(path), org.springframework.http.HttpMethod.PUT, makeRequest(objectToPut), String.class);
        }catch (HttpClientErrorException e){
            response = new ResponseEntity<>(e.getStatusCode());
        }
        lastResponse = response;
    }

    private void putNoBody(String path){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(makeUrl(path), org.springframework.http.HttpMethod.PUT, makeRequest(null), String.class);
        }catch (HttpClientErrorException e){
            response = new ResponseEntity<>(e.getStatusCode());
        }
        lastResponse = response;
    }

    private void delete(String path) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Boolean> response;
        try {
            response = restTemplate.exchange(makeUrl(path), org.springframework.http.HttpMethod.DELETE, makeRequest(null), Boolean.class);
        }catch (HttpClientErrorException e){
            response = new ResponseEntity<>(e.getStatusCode());
        }
        lastResponse = response;
    }

    @Given("i am logged in with email {string} and password {string}")
    public void iAmLoggedIn(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest);
        ResponseEntity<LoginResponse> responseEntity = new RestTemplate().postForEntity(url + port + "/auth/login/employee", entity, LoginResponse.class);
        jwt = responseEntity.getBody().getJwt();
    }

    @Given("customer is logged in with email {string} and password {string}")
    public void customerIsLoggedInWithEmailAndPassword(String email, String password) {
        this.email = email;
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest);
        ResponseEntity<LoginResponse> responseEntity = new RestTemplate().postForEntity(url + port + "/auth/login/customer", entity, LoginResponse.class);
        jwt = responseEntity.getBody().getJwt();
    }


    @Then("response should contain transfer i made")
    public void responseShouldContainTransferIMade() {
        try {
            List<TransferDto> transfers = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<TransferDto>>() {
            });
            assertThat(transfers).isNotEmpty();
            assertThat(transfers).filteredOn(transferDto -> transferDto.getSenderAccountNumber().equals("1234567890") &&
                    transferDto.getRecipientAccountNumber().equals("0987654321") &&
                    transferDto.getAmount().equals(1000.00)).isNotEmpty();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Given("customer is aware of transfer id")
    public void customerIsAwareOfTransferId() {
        List<Transfer> transfer = transferRepository.findAll();

        for(var t:transfer){
            if(t.getSenderBankAccount().getAccountNumber().equals("1234567890") && t.getRecipientBankAccount().getAccountNumber().equals("0987654321") && t.getAmount().equals(1000.00)){
                lastid = t.getId();
                break;
            }
        }

    }

    @Then("response should contain only the transfer i made")
    public void responseShouldContainOnlyTheTransferIMade() {
        TransferDto transferDto = null;
        try {
            transferDto = objectMapper.readValue(lastResponse.getBody().toString(), TransferDto.class);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        assertThat(transferDto).isNotNull();
        assertThat(transferDto.getSenderAccountNumber()).isEqualTo("1234567890");
        assertThat(transferDto.getRecipientAccountNumber()).isEqualTo("0987654321");
        assertThat(transferDto.getAmount()).isEqualTo(1000.00);
    }

    @Given("loanType is {string}")
    public void loantypeIs(String arg0) {
        createLoanRequest.setLoanType(LoanType.valueOf(arg0));
    }

    @Given("loanAmount is {double}")
    public void loanammountIs(double arg) {
        createLoanRequest.setLoanAmount(arg);
    }

    @Given("currency is {string}")
    public void currencyIs(String arg0) {
        createLoanRequest.setCurrency(arg0);
    }

    @Given("loanPurpose is {string}")
    public void loanpurposeIs(String arg0) {
        createLoanRequest.setLoanPurpose(arg0);
    }

    @Given("monthlyIncomeAmount is {string}")
    public void monthlyincomeamountIs(String arg0) {
        createLoanRequest.setMonthlyIncomeAmount(Double.parseDouble(arg0));
    }

    @Given("monthlyIncomeCurrency is {string}")
    public void monthlyincomecurrencyIs(String arg0) {
        createLoanRequest.setMonthlyIncomeCurrency(arg0);
    }

    @Given("permanentEmployee is true")
    public void permanentemployeeIsTrue() {
        createLoanRequest.setPermanentEmployee(true);
    }

    @Given("employmentPeriod is {string}")
    public void employmentperiodIs(String arg0) {
        createLoanRequest.setEmploymentPeriod(Long.parseLong(arg0));
    }

    @Given("loanTerm is {string}")
    public void loantermIs(String arg0) {
        createLoanRequest.setLoanTerm(Long.parseLong(arg0));
    }

    @Given("branchOffice is {string}")
    public void branchofficeIs(String arg0) {
        createLoanRequest.setBranchOffice(arg0);
    }

    @Given("phoneNumber is {string}")
    public void phonenumberIs(String arg0) {
        createLoanRequest.setPhoneNumber(arg0);
    }

    @Given("accountNumber is {string}")
    public void accountnumberIs(String arg0) {
        createLoanRequest.setAccountNumber(arg0);
    }

    @Then("response should be correct loanRequestDto")
    public void responseShouldBeCorrectLoanRequestDto() {
        try{
            LoanRequestDto loanRequestDto = objectMapper.readValue(lastResponse.getBody().toString(), LoanRequestDto.class);
            assertThat(loanRequestDto).isNotNull();
            assertThat(loanRequestDto.getLoanType()).isEqualTo(createLoanRequest.getLoanType());
            assertThat(loanRequestDto.getLoanAmount()).isEqualTo(createLoanRequest.getLoanAmount());
            assertThat(loanRequestDto.getCurrency()).isEqualTo(createLoanRequest.getCurrency());
            assertThat(loanRequestDto.getLoanPurpose()).isEqualTo(createLoanRequest.getLoanPurpose());
            assertThat(loanRequestDto.getMonthlyIncomeAmount()).isEqualTo(createLoanRequest.getMonthlyIncomeAmount());
            assertThat(loanRequestDto.getMonthlyIncomeCurrency()).isEqualTo(createLoanRequest.getMonthlyIncomeCurrency());
            assertThat(loanRequestDto.getPermanentEmployee()).isEqualTo(createLoanRequest.getPermanentEmployee());
            assertThat(loanRequestDto.getEmploymentPeriod()).isEqualTo(createLoanRequest.getEmploymentPeriod());
            assertThat(loanRequestDto.getLoanTerm()).isEqualTo(createLoanRequest.getLoanTerm());
            assertThat(loanRequestDto.getBranchOffice()).isEqualTo(createLoanRequest.getBranchOffice());
            assertThat(loanRequestDto.getPhoneNumber()).isEqualTo(createLoanRequest.getPhoneNumber());
            assertThat(loanRequestDto.getAccountNumber()).isEqualTo(createLoanRequest.getAccountNumber());
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Given("i know which loan id i am changing")
    public void iKnowWhichLoanIdIAmSearching() {
        lastid = loanRequestRepository.findAll().get(0).getId();

    }

    @Then("i should get the correct loan")
    public void iShouldGetTheCorrectLoan() {
        try{
            LoanFullDto loanFullDto = objectMapper.readValue(lastResponse.getBody().toString(), LoanFullDto.class);
            assertThat(loanFullDto).isNotNull();
            assertThat(loanFullDto.getId()).isEqualTo(100);
        }
        catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Then("i should get the correct loans")
    public void iShouldGetTheCorrectLoans() {
        try{
            List<LoanDto> loanDtos = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<LoanDto>>() {
            });
            assertThat(loanDtos).isNotEmpty();
            assertThat(loanDtos).filteredOn(loanDto -> loanDto.getId().equals(100L)).isNotEmpty();
        }
        catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @And("i want to change status to {string}")
    public void iWantToChangeStatusTo(String arg0) {
        statusRequest.setStatus(arg0);
    }

    @Then("loan request status should be {string}")
    public void loanRequestStatusShouldBe(String arg0) {
        LoanRequest loanRequest = loanRequestRepository.findById(lastid).orElse(null);
        assertThat(loanRequest).isNotNull();
        assertThat(loanRequest.getStatus()).isEqualTo(LoanRequestStatus.valueOf(arg0));
    }

    @Given("order type is {string}")
    public void orderTypeIs(String arg0) {
        createOrderRequest.setOrderType(OrderType.valueOf(arg0));
    }

    @And("listingId is {int}")
    public void listingidIs(int arg0) {
        createOrderRequest.setListingId((long) arg0);
    }

    @And("listingType is {string}")
    public void listingtypeIs(String arg0) {
        createOrderRequest.setListingType(ListingType.valueOf(arg0));
    }

    @And("contractSize is {int}")
    public void contractsizeIs(int arg0) {
        createOrderRequest.setContractSize((long) arg0);
    }

    @And("limitValue is {double}")
    public void limitvalueIs(double arg0) {
        createOrderRequest.setLimitValue(arg0);
    }

    @And("stopValue is {double}")
    public void stopvalueIs(double arg0) {
        createOrderRequest.setStopValue(arg0);
    }

    @And("allOrNone is True")
    public void allornoneIsTrue() {
        createOrderRequest.setAllOrNone(true);
    }

    @Then("response should contain all above orders")
    public void responseShouldContainAllAboveOrders() {
        try {
            List<OrderDto> orderDtos = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<OrderDto>>() {
            });
            assertThat(orderDtos).isNotEmpty();
            assertThat(orderDtos).filteredOn(orderDto -> orderDto.getOrderType().equals(OrderType.BUY) &&
                    orderDto.getListingId().equals(100000L));
            assertThat(orderDtos).filteredOn(orderDto -> orderDto.getOrderType().equals(OrderType.BUY) &&
                    orderDto.getListingId().equals(100001L));
            assertThat(orderDtos).filteredOn(orderDto -> orderDto.getOrderType().equals(OrderType.BUY) &&
                    orderDto.getListingId().equals(100002L));
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Given("new accountType is {string}")
    public void newAccountTypeIs(String arg0) {
        bankAccountRequest.setAccountType(AccountType.valueOf(arg0));
    }

    @And("new accountName is {string}")
    public void newAccountNameIs(String arg0) {
        bankAccountRequest.setAccountName(arg0);
    }

    @And("new accountBalance is {string}")
    public void newAccountBalanceIs(String arg0) {
        bankAccountRequest.setBalance(Double.parseDouble(arg0));
    }

    @And("new avaliableBalance is {string}")
    public void newAvaliableBalanceIs(String arg0) {
        bankAccountRequest.setAvailableBalance(Double.parseDouble(arg0));
    }

    @And("new currencyCode is {string}")
    public void newCurrencyCodeIs(String arg0) {
        bankAccountRequest.setCurrencyCode(arg0);
    }

    @And("new subtype is {string}")
    public void newSubtypeIs(String arg0) {
        bankAccountRequest.setSubtypeOfAccount(arg0);
    }

    @And("new maintenanceFee is {double}")
    public void newMaintenanceFeeIs(double arg0) {
        bankAccountRequest.setMaintenanceCost((double) arg0);
    }

    @And("new customerId is {int}")
    public void newCustomerIdIs(int arg0) {
        createBankAccountRequest.setCustomerId((long) arg0);
    }

    @And("new companyId is {int}")
    public void newCompanyIdIs(int arg0) {
        createBankAccountRequest.setCompanyId((long) arg0);
    }

    @And("i should get both bank account i created")
    public void iShouldGetBothBankAccountICreated() {
        try {
            List<BankAccountDto> bankAccountDtos = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<BankAccountDto>>() {
            });
            assertThat(bankAccountDtos).isNotEmpty();
            assertThat(bankAccountDtos).filteredOn(bankAccountDto -> bankAccountDto.getAccountType().equals("CURRENT") &&
                    bankAccountDto.getAccountName().equals("My Account") &&
                    bankAccountDto.getBalance().equals(1000.00) &&
                    bankAccountDto.getAvailableBalance().equals(1000.00)).isNotEmpty();

            assertThat(bankAccountDtos).filteredOn(bankAccountDto -> bankAccountDto.getAccountType().equals("BUSINESS") &&
                    bankAccountDto.getAccountName().equals("BUSINESS Account") &&
                    bankAccountDto.getBalance().equals(1000.00) &&
                    bankAccountDto.getAvailableBalance().equals(1000.00)).isNotEmpty();
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @And("i should get company bank account i created")
    public void iShouldGetCompanyBankAccountICreated() {
        try {
            List<BankAccountDto> bankAccountDtos = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<BankAccountDto>>() {
            });

            assertThat(bankAccountDtos).filteredOn(bankAccountDto -> bankAccountDto.getAccountType().equals("BUSINESS") &&
                    bankAccountDto.getAccountName().equals("BUSINESS Account") &&
                    bankAccountDto.getBalance().equals(1000.00) &&
                    bankAccountDto.getAvailableBalance().equals(1000.00)).isNotEmpty();
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @And("i should get personal bank account i created")
    public void iShouldGetPersonalBankAccountICreated() {
        try {
            List<BankAccountDto> bankAccountDtos = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<BankAccountDto>>() {
            });
            assertThat(bankAccountDtos).isNotEmpty();
            assertThat(bankAccountDtos).filteredOn(bankAccountDto -> bankAccountDto.getAccountType().equals("CURRENT") &&
                    bankAccountDto.getAccountName().equals("My Account") &&
                    bankAccountDto.getBalance().equals(1000.00) &&
                    bankAccountDto.getAvailableBalance().equals(1000.00)).isNotEmpty();
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @And("bankaccountNumber is {string}")
    public void bankaccountnumberIs(String arg0) {
        editBankAccountNameRequest.setBankAccountNumber(arg0);
    }

    @And("new bank account name is {string}")
    public void newBankAccountNameIs(String arg0) {
        editBankAccountNameRequest.setNewName(arg0);
    }

    @And("bank account name should be changed")
    public void bankAcountNameShouldBeChanged() {
        BankAccount bankAccount = bankAccountRepository
                .findBankAccountByAccountNumber(editBankAccountNameRequest.getBankAccountNumber())
                .orElse(null);
        assertThat(bankAccount).isNotNull();
        assertThat(bankAccount.getAccountName()).isEqualTo(editBankAccountNameRequest.getNewName());
    }

    @And("i should get correct customer")
    public void iShouldGetCorrectCustomer() {

    }

    @And("i want to edit customer with email {string}")
    public void iWantToEditCustomerWithEmail(String arg0) {
        editCustomerRequest.setEmail(arg0);
    }

    @And("i want to set customers first name to {string}")
    public void iWantToSetCustomersFirstNameTo(String arg0) {
        editCustomerRequest.setFirstName(arg0);
    }

    @And("customers name should be set to {string}")
    public void customersNameShouldBeSetTo(String arg0) {
        Customer customer = customerRepository.findCustomerByEmail(editCustomerRequest.getEmail()).orElse(null);
        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo(editCustomerRequest.getFirstName());
    }

    @And("employee has first name {string}")
    public void employeeHasFirstName(String arg0) {
        createEmployeeDto.setFirstName(arg0);
    }

    @And("employee has last name {string}")
    public void employeeHasLastName(String arg0) {
        createEmployeeDto.setLastName(arg0);
    }

    @And("employee has phone number {string}")
    public void employeeHasPhoneNumber(String arg0) {
        createEmployeeDto.setPhoneNumber(arg0);
    }

    @And("employee has email {string}")
    public void employeeHasEmail(String arg0) {
        createEmployeeDto.setEmail(arg0);
    }

    @And("employee has jmbg {string}")
    public void employeeHasJmbg(String arg0) {
        createEmployeeDto.setJmbg(arg0);
    }

    @And("employee has position {string}")
    public void employeeHasPosition(String arg0) {
        createEmployeeDto.setPosition(arg0);
    }

    @And("employee is active")
    public void employeeIsActive() {
        createEmployeeDto.setActive(true);
    }

    @And("employee order limit is {string}")
    public void employeeOrderLimitIs(String arg0) {
        createEmployeeDto.setOrderlimit(Double.parseDouble(arg0));
    }

    @And("employee requireApproval is false")
    public void employeeRequireApprovalIsFalse() {
        createEmployeeDto.setRequireApproval(false);
    }

    @And("response should contain new employee")
    public void responseShouldContainNewEmployee() {
        try{
            List<EmployeeDto> employeeDtos = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<EmployeeDto>>() {
            });
            assertThat(employeeDtos).isNotEmpty();
            assertThat(employeeDtos)
                    .filteredOn(employeeDto -> employeeDto.getEmail().equals("drugizaposleni@gmail.rs"))
                    .isNotEmpty();
        }
        catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @And("response should contain correct employee")
    public void responseShouldContainCorrectEmployee() {
        try {
            EmployeeDto employeeDto = objectMapper.readValue(lastResponse.getBody().toString(), EmployeeDto.class);
            assertThat(employeeDto).isNotNull();
            assertThat(employeeDto.getEmail()).isEqualTo("drugizaposleni@gmail.rs");
        }
        catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Given("i got my token using email")
    public void iGotMyTokenUsingEmail() {
        token = employeeRepository.findByEmail("drugizaposleni@gmail.rs").get().getActivationToken();
    }

    @And("my account should be activated and password should be set to {string}")
    public void myAccountShouldBeActivated(String arg0) {
        assertThat(passwordEncoder.matches(arg0, employeeRepository.findByEmail("drugizaposleni@gmail.rs").get().getPassword())).isTrue();
    }

    @And("i want to set my password to {string}")
    public void iWantToSetMyPasswordTo(String arg0) {
        activateAccountRequest.setPassword(arg0);
    }

    @Given("employee wants new password to be {string}")
    public void employeeWantsNewPasswordToBe(String arg0) {
        newPasswordRequest.setPassword(arg0);
    }

    @And("employee got his reset password token using email")
    public void employeeGotHisResetPasswordTokenUsingEmail() {
        token = employeeRepository
                .findByEmail("drugizaposleni@gmail.rs")
                .get()
                .getResetPasswordToken();
    }

    @And("i want to edit employee with email {string}")
    public void iWantToEditEmployeeWithEmail(String arg0) {
        editEmployeeDto.setEmail(arg0);
    }

    @And("i want to set employees first name to {string}")
    public void iWantToSetEmployeesFirstNameTo(String arg0) {
        editEmployeeDto.setFirstName(arg0);
    }

    @And("i want to set employees last name to {string}")
    public void iWantToSetEmployeesLastNameTo(String arg0) {
        editEmployeeDto.setLastName(arg0);
    }

    @And("employee first name should be {string}")
    public void employeeFirstNameShouldBe(String arg0) {
        Employee employee = employeeRepository.findByEmail(editEmployeeDto.getEmail()).orElse(null);
        assertThat(employee).isNotNull();
        assertThat(employee.getFirstName()).isEqualTo(editEmployeeDto.getFirstName());
    }

    @And("employee last name should be {string}")
    public void employeeLastNameShouldBe(String arg0) {
        Employee employee = employeeRepository.findByEmail(editEmployeeDto.getEmail()).orElse(null);
        assertThat(employee).isNotNull();
        assertThat(employee.getLastName()).isEqualTo(editEmployeeDto.getLastName());
    }

    @And("i want to change employee permisions with email {string}")
    public void iWantToChangeEmployeePermisionsWithEmail(String arg0) {
        Employee employee = employeeRepository.findByEmail(arg0).orElse(null);
        lastid = employee.getUserId();
    }

    @And("i want to give him permission {string}")
    public void iWantToGiveHimPermission(String arg0) {
        modifyPermissionsRequest.setPermissions(new ArrayList<>());
        modifyPermissionsRequest.getPermissions().add(arg0);
    }

    @And("employee should have permission {string}")
    public void employeeShouldHavePermission(String arg0) {
        Employee employee = employeeRepository.findById(lastid).orElse(null);
        assertThat(employee).isNotNull();
        assertThat(employee.getPermissions()).isNotEmpty();
        assertThat(employee.getPermissions()).filteredOn(permission -> permission.getName().equals(arg0)).isNotEmpty();
    }

    @And("admin knows id of the employee")
    public void adminKnowsIdOfTheEmployee() {
        lastid = employeeRepository
                .findByEmail("drugizaposleni@gmail.rs")
                .get()
                .getUserId();
    }

    @And("response should contain all permissions of the employee")
    public void responseShouldContainAllPermissionsOfTheEmployee() {
        try{
            List<PermissionDto> permissionDtos = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<PermissionDto>>() {
            });
            assertThat(permissionDtos).isNotEmpty();
            assertThat(permissionDtos).filteredOn(permissionDto -> permissionDto.getName().equals("addUser")).isNotEmpty();
        }
        catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @And("employee limit should be reset")
    public void employeeLimitShouldBeReset() {
        Employee employee = employeeRepository
                .findByEmail("drugizaposleni@gmail.rs")
                .get();
        assertThat(employee.getLimitNow()).isEqualTo(0.0);
    }

    @And("limit is {string}")
    public void limitIs(String arg0) {
        newLimitDto.setLimit(Double.parseDouble(arg0));
    }

    @And("approvalRequired is True")
    public void approvalrequiredIsTrue() {
        newLimitDto.setApprovalRequired(true);
    }

    @And("employee limit should be set to {string}")
    public void employeeLimitShouldBeSetTo(String arg0) {
        Employee employee = employeeRepository.findById(lastid).get();
        assertThat(employee.getOrderlimit()).isEqualTo(Double.parseDouble(arg0));
    }

    @And("response should contain limit of user with email {string}")
    public void responseShouldContainLimitOfUserWithEmail(String arg0) {
        try{
            List<LimitDto> limits = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<LimitDto>>() {
            });
            assertThat(limits).isNotEmpty();
        }
        catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @And("user with email {string} should not exist anymore")
    public void userWithEmailShouldNotExistAnymore(String arg0) {
        Employee employee = employeeRepository.findByEmail(arg0).orElse(null);
        assertThat(employee.getActive()).isFalse();
    }

    @Given("I have a user with id {int}")
    public void iHaveAUserWithId(int id) {
        Employee user = new Employee();
        user.setUserId((long) id);
        user.setEmail("teeeest@gmail.com");
        user.setPassword("testpassword");
        user.setActivationToken(null);
        user.setJmbg("testjmbg");
        user.setActive(true);
//        user.setPermissions(new HashSet<>());
        user.setFirstName("nebitno");
        user.setLastName("nebitno");
//        user.setPosition("nebitno");
        userRepository.save(user);
    }

    @Given("customer wants to send money from account {string} to account {string}")
    public void customerWantsToSendMoneyFromAccountToAccount(String arg0, String arg1) {
        createTransferRequest.setSenderAccountNumber(arg0);
        createTransferRequest.setRecipientAccountNumber(arg1);
    }

    @Given("customer wants to transfer {double}")
    public void customerWantsToTransfer(double arg0) {
        createTransferRequest.setAmount(arg0);
    }

    @Given("there is a permission with name {string}")
    public void thereIsAPermissionWithName(String permission) {

    }

    @Given("user with email {string} exists")
    public void userWithEmailExists(String email) {
        Customer user = new Customer();
        user.setEmail(email);
        user.setPassword("testpassword");
        user.setActivationToken(null);
        user.setJmbg("testjmbg");
        user.setActive(true);
//        user.setPermissions(new HashSet<>());
        user.setFirstName("nebitno");
        user.setLastName("nebitno");
        user.setAccountIds(new ArrayList<>());
//        user.setPosition("nebitno");
        customerRepository.save(user);

        editUserRequest = customerMapper.customerToCustomerResponse(user);
    }

    @Given("user i want to delete exists")
    public void userWithIdExists() {
        Employee user = new Employee();
        user.setEmail("testemail123@gmail.com");
        user.setPassword("testpassword");
        user.setActivationToken(null);
        user.setJmbg("testjmbg12345");
        user.setActive(true);
//        user.setPermissions(new HashSet<>());
        user = userRepository.save(user);
        userToRemove = user.getUserId();
    }

    @Given("admin wants to remove user with id {string}")
    public void adminWantsToRemoveUserWithId(String id) {
        userToRemove = Long.parseLong(id);
    }

    @Given("i have email {string}")
    public void iHaveEmail(String email123) {
        createUserRequest.setEmail(email123);
    }

    @Given("i have firstName {string}")
    public void iHaveFirstName(String firstName) {
        createUserRequest.setFirstName(firstName);
    }

    @Given("i have lastName {string}")
    public void iHaveLastName(String lastName) {
        createUserRequest.setLastName(lastName);
    }

    @Given("i have jmbg {string}")
    public void iHaveJmbg(String jmbg) {
        createUserRequest.setJmbg(jmbg);
    }

    @Given("i have phone number {string}")
    public void iHavePhoneNumber(String phoneNumber) {
        createUserRequest.setPhoneNumber(phoneNumber);
    }
    @Given("i have position {string}")
    public void iHavePosition(String position) {
        createUserRequest.setPosition(position);
    }
    @Given("i am active")
    public void iAmActive() {
        createUserRequest.setActive(true);
    }

    @Given("I am a user that wants to set password to {string}")
    public void iAmAUserThatWantsToSetPasswordTo(String password) {
        this.password = password;
        Customer user = new Customer();
        user.setActivationToken("testtoken");
        user.setEmail("testemail");
        user.setPassword("password");
        user.setActive(true);
        user.setAccountIds(new ArrayList<>());
        customerRepository.save(user);
    }

    @Given("customer got his OTP code")
    public void customerGotHisOTPCode() {
        Customer customer = customerRepository.findCustomerByEmail("user@test.com").orElse(null);

        createPaymentRequest.setSingleUseCode(customer.getSingleUseCode());
    }

    @Given("sender account number is {string}")
    public void senderAccountNumberIs(String arg0) {
        createPaymentRequest.setSenderAccountNumber(arg0);
    }

    @Given("receiver name is {string}")
    public void receiverNameIs(String arg0) {
        createPaymentRequest.setRecipientName(arg0);
    }

    @Given("receiver account number is {string}")
    public void receiverAccountNumberIs(String arg0) {
        createPaymentRequest.setRecipientAccountNumber(arg0);
    }

    @Given("amount is {string}")
    public void amountIs(String arg0) {
        createPaymentRequest.setAmount(Double.parseDouble(arg0));
    }

    @Given("paymentCode is {string}")
    public void paymentcodeIs(String arg0) {
        createPaymentRequest.setPaymentCode(arg0);
    }

    @Given("model is {string}")
    public void modelIs(String arg0) {
        createPaymentRequest.setModel(arg0);
    }

    @Given("referenceNumber is {string}")
    public void referencenumberIs(String arg0) {
        createPaymentRequest.setReferenceNumber(arg0);
    }

    @Given("paymentPurpose is {string}")
    public void paymentpurposeIs(String arg0) {
        createPaymentRequest.setPaymentPurpose(arg0);
    }

    @Given("employee is aware of payment id")
    public void employeeIsAwareOfPaymentId() {
        paymentId = paymentRepository.findAll().get(0).getId();
    }

    @Given("recipient first name is {string}")
    public void recipientFirstNameIs(String arg0) {
        createPaymentRecipientRequest.setFirstName(arg0);
    }

    @Given("recipient last name is {string}")
    public void recipientLastNameIs(String arg0) {
        createPaymentRecipientRequest.setLastName(arg0);
    }

    @Given("recipient bank account number is {string}")
    public void recipientBankAccountNumberIs(String arg0) {
        createPaymentRecipientRequest.setBankAccountNumber(arg0);
    }

    @Given("customer wants to change recipient first name to {string}")
    public void customerWantsToChangeRecipientFirstNameTo(String arg0) {
        post("/recipients/add", createPaymentRecipientRequest);

        paymentRecipientDto.setFirstName(arg0);
        paymentRecipientDto.setLastName(createPaymentRecipientRequest.getLastName());
        paymentRecipientDto.setBankAccountNumber(createPaymentRecipientRequest.getBankAccountNumber());

        PaymentRecipient paymentRecipient = paymentRecipientRepository.findAll().stream().filter(
                recipient -> recipient.getFirstName().equals(createPaymentRecipientRequest.getFirstName()) &&
                        recipient.getLastName().equals(createPaymentRecipientRequest.getLastName()) &&
                        recipient.getRecipientAccountNumber().equals(createPaymentRecipientRequest.getBankAccountNumber())
        ).findFirst().orElse(null);

        if(paymentRecipient == null){
            fail("Recipient not found");
        }

        paymentRecipientDto.setId(paymentRecipient.getId());
    }

//    private String getBody(String path){
//        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
//                .uri(URI.create(path))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + jwt)
//                .method("GET", java.net.http.HttpRequest.BodyPublishers.noBody())
//                .build();
//
//        try {
//            HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            return httpResponse.body();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            fail("Http GET request error");
//            return "";
//        }
//    }


   @When("I go to {string}")
    public void iGoTo(String path) {
        activatedUser = userRepository.findByActivationToken("testtoken").get();
       ActivateAccountRequest activateAccountRequest = new ActivateAccountRequest();
       activateAccountRequest.setPassword(password);
       ObjectMapper objectMapper = new ObjectMapper();
       try {
           lastActivateAccountResponse = objectMapper.readValue(post(path, activateAccountRequest), ActivateAccountResponse.class);
       } catch (Exception e) {
           e.printStackTrace();
           fail("Failed to parse response body");
       }
   }

   @When("i select user with email {string} to change")
   public void whenISelectUserWithEmailToChange(String email) {
       editUserRequest.setEmail(email);
   }

   @When("i change first name to {string}")
    public void whenIChangeFirstNameTo(String firstName) {
         editUserRequest.setFirstName(firstName);
    }


    @Then("i should get my id as a response")
    public void iShouldGetMyIdAsAResponse() {
        assertThat(lastCreateUserResponse.getUserId()).isNotNull();
    }

    @Then("email should be sent to me")
    public void emailShouldBeSentToMe() {
        verify(emailService).sendEmail(eq(createUserRequest.getEmail()), anyString(), anyString());
    }

    @Given("user provides email {string}")
    public void userProvidesEmail(String email) {
        searchFilter.setEmail(email);
    }

    @Given("user provides first name {string}")
    public void userProvidesFirstName(String firstName) {
        searchFilter.setFirstName(firstName);
    }

    @Given("user provides last name {string}")
    public void userProvidesLastName(String lastName) {
        searchFilter.setLastName(lastName);
    }

    @Given("user provides position {string}")
    public void userProvidesPosition(String position) {
        searchFilter.setPosition(position);
    }

    @Then("Response body is the correct JSON list of users")
    public void theResponseBodyShouldBeAListOfUsers() {
        assertThat(lastReadAllCustomersResponse).hasSameElementsAs(customerResponses);
    }

    @Then("Response body is the correct user JSON")
    public void responseBodyIsTheCorrectUserJSON() {
        if(email!=null) {
            EmployeeDto userResponse = userMapper.employeeToEmployeeDto(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));
            assertThat(lastReadUserResponse).isEqualTo(userResponse);
        }
        else {
            EmployeeDto userResponse = userMapper.employeeToEmployeeDto(userRepository.findById(lastid).get());
            assertThat(lastReadUserResponse).isEqualTo(userResponse);
        }
    }

    @Then("I should have my password set to {string}")
    public void iShouldHaveMyPasswordSetTo(String password) {
        activatedUser = userRepository.findById(lastActivateAccountResponse.getUserId()).get();
        assertThat(passwordEncoder.matches(password, activatedUser.getPassword())).isTrue();
    }
    @Then("customer should have his password set to {string}")
    public void customerShouldHaveHisPasswordSetTo(String arg0) {
        assertThat(passwordEncoder.matches(arg0, customerRepository.findCustomerByEmail(customerData.getEmail()).get().getPassword())).isTrue();
    }


    @Then("user with email {string} has his first name changed to {string}")
    public void userWithEmailHasHisFirstNameChangedTo(String email, String firstName) {
        Customer user = customerRepository.findCustomerByEmail(email).get();
        assertThat(user.getFirstName()).isEqualTo(firstName);
    }

    @Then("user is removed from the system")
    public void userWithIdIsRemoved() {
        assertThat(userRepository.findById(userToRemove).get().getActive()).isFalse();
    }

    @Then("i should get response with status {int}")
    public void iShouldGetResponseWithStatus(int status) {
        assertThat(lastResponse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.valueOf(status));
    }

    @Given("customer has first name {string}")
    public void customerHasFirstName(String arg0) {
        customerData.setFirstName(arg0);
    }

    @Given("customer has last name {string}")
    public void customerHasLastName(String arg0) {
        customerData.setLastName(arg0);
    }

    @Given("customer has date of birth of {string}")
    public void customerHasDateOfBirthOf(String arg0) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date = df.parse(arg0);
        customerData.setDateOfBirth((long)date.getTime());
    }

    @Given("customer has address {string}")
    public void customerHasAddress(String arg0) {
        customerData.setAddress(arg0);
    }

    @Given("customer has phone number {string}")
    public void customerHasPhoneNumber(String arg0) {
        customerData.setPhoneNumber(arg0);
    }

    @Given("customer has email {string}")
    public void customerHasEmail(String arg0) {
        customerData.setEmail(arg0);
    }

    @Given("customer has jmbg {string}")
    public void customerHasJmbg(String arg0) {
        customerData.setJmbg(arg0);
    }

    @Given("customer is male")
    public void customerIsMale() {
        customerData.setGender("M");
    }

    @Given("accountType is {string}")
    public void accounttypeIs(String arg0) {
        accountData.setAccountType(AccountType.valueOf(arg0));
    }

    @Given("account currency is {string}")
    public void accountCurrencyIs(String arg0) {
        accountData.setCurrencyCode(arg0);
    }

    @Given("maintenance cost is {string}")
    public void maintenanceCostIs(String arg0) {
        accountData.setMaintenanceCost(Double.parseDouble(arg0));
    }

    @Then("response should be true")
    public void responseShouldBeTrue() {
        assertThat(lastResponse.getBody()).isEqualTo("true");
    }

    @Given("customer got his bank account from email")
    public void customerGotHisBankAccountFromEmail() {
        Customer customer = customerRepository.findCustomerByEmail(customerData.getEmail()).get();
        bankAccountNumber = bankAccountRepository.findByCustomer(customer).getFirst().getAccountNumber();
    }

    @Given("customer got his token from email")
    public void customerGotHisTokenFromEmail() {
        token = customerRepository.findCustomerByEmail(customerData.getEmail()).get().getActivationToken();
    }

    @Given("customer wants to set his password to {string}")
    public void customerWantsToSetHisPasswordTo(String arg0) {
        password = arg0;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @Then("i should get all customers")
    public void iShouldGetAllCustomers() {
        try{
            CustomerMapper customerMapper = new CustomerMapper(new PermissionMapper(), new BankAccountMapper());
            List<CustomerResponse> customerResponses = objectMapper.readValue(lastResponse.getBody().toString(), new TypeReference<List<CustomerResponse>>() {
            });
            List<Customer> customers = customerRepository.findAll();
            List<CustomerResponse> customerResponses1 = new ArrayList<>();
            customers.forEach(customer -> customerResponses1.add(customerMapper.customerToCustomerResponse(customer)));
            assertThat(customerResponses).hasSameElementsAs(customerResponses1);
        } catch (Exception e){
            e.printStackTrace();
            fail("Failed to parse response body");
        }
    }

    @Then("i should have my OTP code set")
    public void iShouldHaveMyOTPCodeSet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null)
            return;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        CustomerResponse response = this.customerRepository.findCustomerByEmail(email)
                .map(this.customerMapper::customerToCustomerResponse)
                .orElse(null);
        customerRepository.findCustomerByEmail(email).ifPresent(user -> {
            assertThat(user.getSingleUseCode()).isNotNull();
        });
    }


    @Then("response should contain payment with receiver account number {string}")
    public void responseShouldContainPaymentWithReceiverAccountNumber(String arg0) throws JsonProcessingException {
        List<PaymentDto> payments =  objectMapper.readValue((String)lastResponse.getBody(), new TypeReference<List<PaymentDto>>() {
        });
        assertThat(payments).anyMatch(payment -> payment.getRecipientAccountNumber().equals(arg0));
    }

    @Then("i should NOT have recipient mika mikic {string} in response")
    public void iShouldNOTHaveRecipientMikaMikicInResponse(String arg0) throws JsonProcessingException {

        List<PaymentRecipientDto> answer = objectMapper.readValue((String)lastResponse.getBody(), new TypeReference<List<PaymentRecipientDto>>() {
        });

        for(PaymentRecipientDto recipient : answer){
            if(recipient.getFirstName().equals("mika") &&
                    recipient.getLastName().equals("mikic") &&
                    recipient.getBankAccountNumber().equals(arg0)){
                fail("Recipient found");
            }
        }
    }

    @Then("recipient first name should be {string}")
    public void recipientFirstNameShouldBe(String arg0) {
        PaymentRecipient paymentRecipient = paymentRecipientRepository.findById(paymentRecipientDto.getId()).orElse(null);
        if(paymentRecipient == null){
            fail("Recipient not found");
        }

        assertThat(paymentRecipient.getFirstName()).isEqualTo(arg0);
    }


//    @Then("new foreign account should be created")
//    public void newForeignAccountShouldBeCreated() {
//        assertThat(lastCreateForeignCurrencyAccountResponse).isNotNull();
//        assertThat(foreignCurrencyAccountRepository.findById(lastCreateForeignCurrencyAccountResponse.getId())).isNotNull();
//    }
//
//    @Then("i should get all foreign accounts")
//    public void iShouldGetAllForeignAccounts() {
//        ForeignCurrencyAccountMapper mapper = new ForeignCurrencyAccountMapper();
//        List<ForeignCurrencyAccountResponse> foreignCurrencyAccountResponses = new ArrayList<>();
//        foreignCurrencyAccountRepository.findAll().forEach(
//                x->{
//                    foreignCurrencyAccountResponses.add(mapper.foreignCurrencyAccountToForeignCurrencyAccountResponse(x));
//                }
//        );
//        assertThat(lastReadAllForeignCurrencyAccountsResponse).hasSameElementsAs(foreignCurrencyAccountResponses);
//    }
}
