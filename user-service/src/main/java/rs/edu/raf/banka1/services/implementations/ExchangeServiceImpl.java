package rs.edu.raf.banka1.services.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ExchangeRate;
import rs.edu.raf.banka1.model.Transfer;
import rs.edu.raf.banka1.repositories.ExchangeRateRepository;
import rs.edu.raf.banka1.repositories.TransferRepository;
import rs.edu.raf.banka1.requests.CreateTransferRequest;
import rs.edu.raf.banka1.services.ExchangeService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRateRepository exchangeRateRepository;

    private final TransferRepository transferRepository;
    private final ObjectMapper objectMapper;

    @Value("${exchangeRateAPIToken}")
    private String exchangeRateAPIToken;

    @Value("${exchangeRateApiUrl}")
    private String exchangeRateApiUrl;

    public ExchangeServiceImpl(ExchangeRateRepository exchangeRateRepository,TransferRepository transferRepository) {
        objectMapper = new ObjectMapper();
        this.exchangeRateRepository = exchangeRateRepository;
        this.transferRepository = transferRepository;
    }

    public List<ExchangeRate> fetchExchangeRates(String fromCurrencyCode){
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(exchangeRateApiUrl + exchangeRateAPIToken+"/latest/"+fromCurrencyCode))
                .GET()
                .build();

        List<ExchangeRate> rates = new ArrayList<>();

        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                String  baseCode = rootNode.get("base_code").asText();
                JsonNode conversionRateNode = rootNode.get("conversion_rates");
                double usdRate = conversionRateNode.get("USD").asDouble();
                double audRate = conversionRateNode.get("AUD").asDouble();
                double eurRate = conversionRateNode.get("EUR").asDouble();
                double chfRate = conversionRateNode.get("CHF").asDouble();
                double gbpRate = conversionRateNode.get("GBP").asDouble();
                double jpyRate = conversionRateNode.get("JPY").asDouble();
                double cadRate = conversionRateNode.get("CAD").asDouble();

            }else{
                System.out.println("Error response code" + response.statusCode());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return rates;
    }

    @Override
    public List<ExchangeRate> getExchangeRates(String fromCurrencyCode) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(exchangeRateApiUrl + exchangeRateAPIToken+"/latest/"+fromCurrencyCode))
                .GET()
                .build();

        List<ExchangeRate> rates = new ArrayList<>();
        //EUR, CHF, USD, GBP, JPY, CAD i AUD
        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
               // System.out.println(response.body());
                String responseBody = response.body();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                String  baseCode = rootNode.get("base_code").asText();
                JsonNode conversionRateNode = rootNode.get("conversion_rates");
                double usdRate = conversionRateNode.get("USD").asDouble();
                double audRate = conversionRateNode.get("AUD").asDouble();
                double eurRate = conversionRateNode.get("EUR").asDouble();
                double chfRate = conversionRateNode.get("CHF").asDouble();
                double gbpRate = conversionRateNode.get("GBP").asDouble();
                double jpyRate = conversionRateNode.get("JPY").asDouble();
                double cadRate = conversionRateNode.get("CAD").asDouble();
                System.out.println(usdRate+" "+ audRate+" "+cadRate+" "+" [API]");

            }else{
                System.out.println("Error response code" + response.statusCode());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
//        ExchangeRate exchangeRate = new ExchangeRate();
//        exchangeRate.setCurrencyFrom(fromCurrencyCode);
//        exchangeRate.setCurrencyTo(toCurrency);
//        exchangeRate.setRate();
//
//        rateRepository.save(exchangeRate);
        return rates;
    }

    @Override
    public Transfer createTransfer(CreateTransferRequest createTransferRequest) {
       return null;
    }

}
