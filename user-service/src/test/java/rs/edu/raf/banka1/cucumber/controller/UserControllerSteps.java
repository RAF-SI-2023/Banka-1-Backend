package rs.edu.raf.banka1.cucumber.controller;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerSteps {
    @LocalServerPort
    private String port;

    private ResponseEntity<UserResponse> lastReadUserResponse;
    private ResponseEntity<List<UserResponse>> lastReadAllUsersResponse;
    private UserMapper userMapper = new UserMapper();


    private UserRepository userRepository;

    private final String url = "http://localhost:";

    public UserControllerSteps(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @When("User calls get on {string}")
    public void iSendAGETRequestTo(String path) {
        if(path.equals("/user/getAll"))
            lastReadAllUsersResponse = new RestTemplate().exchange(url + port + path, org.springframework.http.HttpMethod.GET, null,  new ParameterizedTypeReference<List<UserResponse>>() {});
        else if(path.startsWith("user/get"))
            lastReadUserResponse = new RestTemplate().exchange(url + port + path, org.springframework.http.HttpMethod.GET, null, UserResponse.class);
    }

    @Then("Response status is {string}")
    public void theResponseStatusShouldBe(String code) {
        // Write code here that turns the phrase above into concrete actions
        assertThat(lastReadAllUsersResponse.getStatusCode().toString()).isEqualTo(code);
    }

    @Then("Response body is the correct JSON list of users")
    public void theResponseBodyShouldBeAListOfUsers() {
        List<UserResponse> userResponses = new ArrayList<>();
        userRepository.findAll().forEach(user -> userResponses.add(userMapper.userToUserResponse(user)));
        assertThat(lastReadAllUsersResponse.getBody()).hasSameElementsAs(userResponses);
    }
}
