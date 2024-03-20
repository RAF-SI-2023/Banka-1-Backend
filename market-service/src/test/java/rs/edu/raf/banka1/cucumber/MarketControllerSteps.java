package rs.edu.raf.banka1.cucumber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.mapper.ExchangeMapper;
import rs.edu.raf.banka1.model.dtos.ExchangeDto;
import rs.edu.raf.banka1.model.dtos.LoginRequest;
import rs.edu.raf.banka1.model.dtos.LoginResponse;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
import rs.edu.raf.banka1.model.entities.Inflation;
import rs.edu.raf.banka1.repositories.ExchangeRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MarketControllerSteps {
    private ExchangeRepository exchangeRepository;
    private ExchangeMapper exchangeMapper;
    private final String userurl = "http://" + SpringIntegrationTest.enviroment.getServiceHost("user-service", 8080) + ":";
    private String userport = Integer.toString(SpringIntegrationTest.enviroment.getServicePort("user-service", 8080));
    private final String marketurl = "http://" + SpringIntegrationTest.enviroment.getServiceHost("market-service", 8081) + ":";
    private String marketport = Integer.toString(SpringIntegrationTest.enviroment.getServicePort("market-service", 8081));
    private String jwt;
    private ResponseEntity<?> lastResponse;
    private ExchangeDto lastExchangeDto;
    private Object lastObject;
    private List<OptionsDto> lastOptionsResponse;

    private List<Inflation> lastInflationResponse;


    protected static class Ticker{
        public String ticker;
        public Integer timestampFrom;
        public Integer timestampTo;
    }

    private Ticker ticker = new Ticker();

    public MarketControllerSteps(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Given("i am logged in with email {string} and password {string}")
    public void iAmLoggedIn(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest);
        ResponseEntity<LoginResponse> responseEntity = new RestTemplate().postForEntity(userurl + userport + "/auth/login", entity, LoginResponse.class);
        jwt = responseEntity.getBody().getJwt();
    }

    @Given("i have a ticker {string}")
    public void iHaveATicker(String arg0) {
        ticker.ticker = arg0;
    }

    @Given("i have a timestamp from {string} and to {string}")
    public void iHaveATimestampFromAndTo(String arg0, String arg1) {
        ticker.timestampFrom = Integer.parseInt(arg0);
        ticker.timestampTo = Integer.parseInt(arg1);
    }

    private String get(String url){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, String.class);
        lastResponse = response;
        return response.getBody();
    }

    @When("i send GET request to {string}")
    public void iSendGETRequestTo(String url) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //lastReadAllUsersResponse = objectMapper.readValue(getBody(url + port + path), new TypeReference<List<UserResponse>>() {});
            if (url.equals("/market/currency/100000/inflation")) {
                System.out.println("todo");
            } else if (url.equals("/market/exchange") || url.equals("/market/listing/")) {
                get(marketurl + marketport + url);
            }
            else if(url.equals("/market/exchange/100000")){
                lastExchangeDto = objectMapper.readValue(get(marketurl + marketport + url), ExchangeDto.class);
                lastObject = exchangeRepository.findById(100000L).orElseThrow(()->new RuntimeException("Exchange not found"));
            }
            else if(url.equals("/market/listing/history/")){
                url = url.concat("?ticker=").concat(ticker.ticker).concat("&timestampFrom=").concat(ticker.timestampFrom.toString()).concat("&timestampTo=").concat(ticker.timestampTo.toString());
                get(marketurl + marketport + url);
            }
            else if(url.equals("/market/listing/forex")){
                get(marketurl + marketport + url);
            }
            else if(url.equals("/options/testticker")){
                get(marketurl + marketport + url);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Then("i should get response with status {int}")
    public void iShouldGetResponseWithStatus(int status) {
        assertThat(lastResponse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.valueOf(status));
    }

    @Then("Response body is the correct exchange JSON")
    public void responseBodyIsTheCorrectExchangeJSON() {
        assertThat(lastExchangeDto).isNotNull();
        if(lastObject instanceof ExchangeDto)
            assertThat(lastExchangeDto).isEqualTo(exchangeMapper.exchangeToExchangeDto((rs.edu.raf.banka1.model.entities.Exchange) lastObject));
    }
}
