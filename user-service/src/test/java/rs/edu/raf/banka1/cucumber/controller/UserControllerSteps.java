package rs.edu.raf.banka1.cucumber.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.cucumber.SpringIntegrationTest;
//import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.User;
//import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.requests.*;
import rs.edu.raf.banka1.responses.*;
import rs.edu.raf.banka1.services.EmailService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerSteps {

    @Autowired
    private EmailService emailService;
    //@LocalServerPort
    //private String port;

    private String port = Integer.toString(SpringIntegrationTest.enviroment.getServicePort("user-service", 8080));
    //private String port = "8080";

    private String jwt = "";

    private UserResponse lastReadUserResponse;
    private List<UserResponse> lastReadAllUsersResponse;
    private CreateUserResponse lastCreateUserResponse;
//    private CreateForeignCurrencyAccountResponse lastCreateForeignCurrencyAccountResponse;
//    private List<ForeignCurrencyAccountResponse> lastReadAllForeignCurrencyAccountsResponse;
    private EditUserResponse lastEditUserResponse;
    private ActivateAccountResponse lastActivateAccountResponse;
    private User activatedUser;
    private EditUserRequest editUserRequest = new EditUserRequest();
    private CreateUserRequest createUserRequest = new CreateUserRequest();
    private Long userToRemove;
    private String email;
    private UserMapper userMapper = new UserMapper(new PermissionMapper());
    private ResponseEntity<?> lastResponse;

    private UserRepository userRepository;
//    private ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
//    private ForeignCurrencyAccountRequest foreignCurrencyAccountRequest = new ForeignCurrencyAccountRequest();
    private PermissionRepository permissionRepository;
    private List<UserResponse> userResponses = new ArrayList<>();
    //private final String url = "http://localhost:";
    private final String url = "http://" + SpringIntegrationTest.enviroment.getServiceHost("user-service", 8080) + ":";
    //private final String url = "http://" + "host.docker.internal" + ":";
    private Long lastid;
    private String password;


    @Data
    class SearchFilter {
        private String email;
        private String firstName;
        private String lastName;
        private String position;
    }

    private SearchFilter searchFilter = new SearchFilter();

//    @Given("ownerId is {string}")
//    public void owneridIs(String arg0) {
//        foreignCurrencyAccountRequest.setOwnerId(Long.parseLong(arg0));
//    }
//
//    @Given("createdByAgentId is {string}")
//    public void createdbyagentidIs(String arg0) {
//        foreignCurrencyAccountRequest.setCreatedByAgentId(Long.parseLong(arg0));
//    }
//
//    @Given("currency is {string}")
//    public void currencyIs(String arg0) {
//        foreignCurrencyAccountRequest.setCurrency(arg0);
//    }
//
//    @Given("subtypeOfAccount is {string}")
//    public void subtypeofaccountIs(String arg0) {
//        foreignCurrencyAccountRequest.setSubtypeOfAccount(arg0);
//    }
////    @Given("typeOfAccount is {string}")
////    public void typeofaccountIs(String arg0) {
////        foreignCurrencyAccountRequest.setTypeOfAccount(arg0);
////    }
//    @Given("accountMaintenance is {string}")
//    public void accountmaintenanceIs(String arg0) {
//        foreignCurrencyAccountRequest.setAccountMaintenance(Double.parseDouble(arg0));
//    }
//    @Given("defaultCurrency is {string}")
//    public void defaultcurrencyIs(String arg0) {
//        foreignCurrencyAccountRequest.setDefaultCurrency(Boolean.valueOf(arg0));
//    }
//    @Given("allowedCurrencies is {string}")
//    public void allowedcurrenciesIs(String arg0) {
//        List<String> allowedCurrencies = new ArrayList<>();
//        allowedCurrencies.add(arg0);
//        foreignCurrencyAccountRequest.setAllowedCurrencies(allowedCurrencies);
//    }

    @Given("i am logged in with email {string} and password {string}")
    public void iAmLoggedIn(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest);
        ResponseEntity<LoginResponse> responseEntity = new RestTemplate().postForEntity(url + port + "/auth/login", entity, LoginResponse.class);
        jwt = responseEntity.getBody().getJwt();
    }

    @Given("I have a user with id {int}")
    public void iHaveAUserWithId(int id) {
        User user = new User();
        user.setUserId((long) id);
        user.setEmail("teeeest@gmail.com");
        user.setPassword("testpassword");
        user.setActivationToken(null);
        user.setJmbg("testjmbg");
        user.setActive(true);
        user.setPermissions(new HashSet<>());
        user.setFirstName("nebitno");
        user.setLastName("nebitno");
        user.setPosition("nebitno");
        userRepository.save(user);
    }

    @Given("there is a permission with name {string}")
    public void thereIsAPermissionWithName(String permission) {

    }

    @Given("user with email {string} exists")
    public void userWithEmailExists(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("testpassword");
        user.setActivationToken(null);
        user.setJmbg("testjmbg");
        user.setActive(true);
        user.setPermissions(new HashSet<>());
        user.setFirstName("nebitno");
        user.setLastName("nebitno");
        user.setPosition("nebitno");
        userRepository.save(user);

        editUserRequest = userMapper.userToEditUserRequest(user);
    }

    @Given("user i want to delete exists")
    public void userWithIdExists() {
        User user = new User();
        user.setEmail("testemail123@gmail.com");
        user.setPassword("testpassword");
        user.setActivationToken(null);
        user.setJmbg("testjmbg12345");
        user.setActive(true);
        user.setPermissions(new HashSet<>());
        user = userRepository.save(user);
        userToRemove = user.getUserId();
    }

    @Given("admin wants to remove user with id {string}")
    public void adminWantsToRemoveUserWithId(String id) {
        userToRemove = Long.parseLong(id);
    }

    public UserControllerSteps(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Given("i have email {string}")
    public void iHaveEmail(String email123) {
        createUserRequest.mysetEmail(email123);
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
        User user = new User();
        user.setActivationToken("testtoken");
        user.setEmail("testemail");
        user.setPassword("testpassword");
        user.setActive(true);
        userRepository.save(user);
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

    private String getBody(String path){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(path, org.springframework.http.HttpMethod.GET, request, String.class);
        lastResponse = response;
        return response.getBody();
    }

    private String post(String path, Object objectToPost){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<Object> request = new HttpEntity<>(objectToPost, headers);

        ResponseEntity<String> response = restTemplate.exchange(path, org.springframework.http.HttpMethod.POST, request, String.class);
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(path, org.springframework.http.HttpMethod.GET, request, String.class);
        lastResponse = response;
        return response.getBody();
    }


    private void put(String path, Object objectToPut){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<Object> request = new HttpEntity<>(objectToPut, headers);

        lastResponse = restTemplate.exchange(path, org.springframework.http.HttpMethod.PUT, request, String.class);
    }

    private void delete(String path) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        lastResponse = restTemplate.exchange(path, org.springframework.http.HttpMethod.DELETE, request, Boolean.class);
    }


    @When("User calls get on {string}")
    public void iSendAGETRequestTo(String path) {
        userResponses.clear();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (path.equals("/user/getAll")) {
                lastReadAllUsersResponse = objectMapper.readValue(getBody(url + port + path), new TypeReference<List<UserResponse>>() {
                });
                userRepository.findAll().forEach(user -> userResponses.add(userMapper.userToUserResponse(user)));
            }
            else if (path.startsWith("/user/get/")) {
                    lastReadUserResponse = objectMapper.readValue(getBody(url + port + path), UserResponse.class);
                String[] split = path.split("/");
                email = split[split.length - 1];
            }
            else if (path.equals("/user/search")) {
                lastReadAllUsersResponse = objectMapper.readValue(getFiltered(url + port + path), new TypeReference<List<UserResponse>>() {
                });
                userRepository.findAll().forEach(user -> {
                    if (!user.getActive()) return;
                    if (searchFilter.getEmail() != null && !user.getEmail().equals(searchFilter.getEmail())) return;
                    if (searchFilter.getFirstName() != null && !user.getFirstName().equalsIgnoreCase(searchFilter.getFirstName()))
                        return;
                    if (searchFilter.getLastName() != null && !user.getLastName().equalsIgnoreCase(searchFilter.getLastName()))
                        return;
                    if (searchFilter.getPosition() != null && !user.getPosition().equalsIgnoreCase(searchFilter.getPosition()))
                        return;
                    userResponses.add(userMapper.userToUserResponse(user));
                });
            }
            else if (path.equals("/user/permissions/userId/100") || path.equals("/user/permissions/email/admin@admin.com")) {
                getBody(url + port + path);
            }
            else if (path.equals("/balance/foreign_currency/100")) {
                getBody(url + port + path);
            }
//            else if (path.equals("/balance/foreign_currency")) {
//                lastReadAllForeignCurrencyAccountsResponse = objectMapper.readValue(getBody(url + port + path), new TypeReference<List<ForeignCurrencyAccountResponse>>() {
//                });
//            }
            else if (path.startsWith("/user/")) {
                lastReadUserResponse = objectMapper.readValue(getBody(url + port + path), UserResponse.class);
                String[] split = path.split("/");
                lastid = Long.parseLong(split[split.length - 1]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

   @When("user calls POST on {string}")
   public void userCallsPostOn(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (path.equals("/user/createUser")) {
                String tmp = post(url + port + path, createUserRequest);
                lastCreateUserResponse = objectMapper.readValue(tmp, CreateUserResponse.class);
            }
//            else if (path.equals("/balance/foreign_currency/create")) {
//                lastCreateForeignCurrencyAccountResponse = objectMapper.readValue(post(url + port + path, foreignCurrencyAccountRequest), CreateForeignCurrencyAccountResponse.class);
//            }
        }
        catch (Exception e){
            e.printStackTrace();
            fail("Failed to parse response body");
        }
   }

    @When("i send DELETE request to remove the user")
    public void iSendDELETERequestTo() {
        delete(url + port + "/user/remove/" + userToRemove);
    }

   @When("I go to {string}")
    public void iGoTo(String path) {
        activatedUser = userRepository.findByActivationToken("testtoken").get();
       ActivateAccountRequest activateAccountRequest = new ActivateAccountRequest();
       activateAccountRequest.setPassword(password);
       ObjectMapper objectMapper = new ObjectMapper();
       try {
           lastActivateAccountResponse = objectMapper.readValue(post(url + port + path, activateAccountRequest), ActivateAccountResponse.class);
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

    @When("i send PUT request to {string}")
    public void whenISendPUTRequestTo(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            put(url + port + path, editUserRequest);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to parse response body");
        }
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
        assertThat(lastReadAllUsersResponse).hasSameElementsAs(userResponses);
    }

    @Then("Response body is the correct user JSON")
    public void responseBodyIsTheCorrectUserJSON() {
        if(email!=null) {
            UserResponse userResponse = userMapper.userToUserResponse(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));
            assertThat(lastReadUserResponse).isEqualTo(userResponse);
        }
        else {
            UserResponse userResponse = userMapper.userToUserResponse(userRepository.findById(lastid).get());
            assertThat(lastReadUserResponse).isEqualTo(userResponse);
        }
    }

    @Then("I should have my password set to {string}")
    public void iShouldHaveMyPasswordSetTo(String password) {
        activatedUser = userRepository.findById(activatedUser.getUserId()).get();
        assertThat(activatedUser.getPassword()).isEqualTo(password);
    }

    @Then("user with email {string} has his first name changed to {string}")
    public void userWithEmailHasHisFirstNameChangedTo(String email, String firstName) {
        User user = userRepository.findByEmail(email).get();
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
