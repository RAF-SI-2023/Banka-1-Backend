package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.exceptions.OptionsException;
import rs.edu.raf.banka1.mapper.OptionsMapper;
import rs.edu.raf.banka1.model.OptionsModel;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
import rs.edu.raf.banka1.model.enums.OptionType;
import rs.edu.raf.banka1.repositories.OptionsRepository;
import rs.edu.raf.banka1.utils.Constants;

import java.io.File;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OptionsServiceImpl implements OptionsService{
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient = HttpClient.newHttpClient();
    private HttpRequest httpRequest;
    private String cookie = null;
    private String crumb = null;
    @Value("${optionsUrl}")
    private String optionsUrl;
    private String urlWithCrumb = null;
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
    private OptionsRepository optionsRepository;
    private OptionsMapper optionsMapper;

    public OptionsServiceImpl(OptionsRepository optionsRepository,
                              OptionsMapper optionsMapper) {
        this.optionsRepository = optionsRepository;
        this.optionsMapper = optionsMapper;
    }

    @Override
    public List<OptionsDto> fetchOptions() {
        if (cookie == null || crumb == null) {
            // If not cached, obtain them
            if (!getCookieAndCrumb()) {
                System.out.println("Failed to obtain cookie and crumb values");
                return new ArrayList<>();
            }
        }

        List<OptionsDto> optionsModels = new ArrayList<>();
        try{
            List<String> tickers = fetchTickers();
//             Constants.tickersForTestingOptions
            for (String ticker : tickers)
                optionsModels.addAll(fetchOptionsForTicker(ticker, optionsUrl + ticker + "?crumb=" + crumb));

            // Uncomment when filling the options.json
//            File file = new File(Constants.optionsFilePath);
//            objectMapper.writeValue(file, optionsModels);

            return optionsModels;
        }catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    List<String> fetchTickers() {
        try {
            File file = new File(Constants.listingsFilePath);

            // Read JSON data from the file
            JsonNode rootNode = objectMapper.readTree(file);
            List<String> tickers = new ArrayList<>();
            // Iterate over each element in the JSON array
            for (JsonNode node : rootNode) {
                tickers.add(node.path("symbol").asText());
            }
//
            return tickers;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    @Override
    public List<OptionsDto> fetchOptionsForTicker(String ticker, String url) {
        httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url)) //url with crumb !!!
                .header("Cookie", cookie) // Include the cached cookie in the request
                .GET()
                .build();

        List<OptionsModel> options = new ArrayList<>();

        try {
            // Send the request to retrieve data with crumb value
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Print the response body
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                if(rootNode.path("optionChain").path("result").isEmpty() || rootNode.path("optionChain").path("result").get(0).path("options").isEmpty()) {
                    return new ArrayList<>();
                }

                JsonNode callsNode = rootNode.path("optionChain").path("result").get(0).path("options").get(0).path("calls");
                JsonNode putsNode = rootNode.path("optionChain").path("result").get(0).path("options").get(0).path("puts");

                //calls
                Thread callsThread = new Thread(() -> {
                    try {
                        List<OptionsModel> callsOptions = parseOptions(callsNode, ticker, OptionType.CALL);
                        options.addAll(callsOptions);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                // PUTS
                Thread putsThread = new Thread(() -> {
                    try {
                        List<OptionsModel> putsOptions = parseOptions(putsNode, ticker, OptionType.PUT);
                        options.addAll(putsOptions);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                callsThread.start();
                putsThread.start();

                callsThread.join();
                putsThread.join();

                optionsRepository.saveAll(options);

            } else {
                throw new OptionsException("Failed to retrieve data for ticker: " + ticker + ". Status code: " + response.statusCode());
            }
        } catch (Exception ignored) {}
        return options.stream().map((optionsModel) -> optionsMapper.optionsModelToOptionsDto(optionsModel)).toList();
    }

    @Override
    public List<OptionsDto> getOptionsByTicker(String ticker) {
        return this.optionsRepository.findByTicker(ticker).map(optionsModels ->
            optionsModels.stream()
                .map(optionsMapper::optionsModelToOptionsDto)
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
    }

    private List<OptionsModel> parseOptions(JsonNode optionsNode, String ticker, OptionType optionType) {
        List<OptionsModel> options = new ArrayList<>();
        Iterator<JsonNode> optionsIterator = optionsNode.elements();
        while(optionsIterator.hasNext()) {
            JsonNode row = optionsIterator.next();
            OptionsModel option = new OptionsModel();
            option.setTicker(ticker);
            option.setOptionType(optionType.name());
            option.setStrikePrice(row.path("strike").doubleValue());
            option.setCurrency(row.path("currency").asText());
            option.setImpliedVolatility(row.path("impliedVolatility").doubleValue());
            option.setOpenInterest(row.path("openInterest").asInt());
            option.setExpirationDate(row.path("expiration").longValue());
            options.add(option);
        }
        return options;
    }

    private boolean getCookieAndCrumb() {
        String initialUrl = "https://fc.yahoo.com";
        httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(initialUrl))
                .GET()
                .build();

        try {
            // Dohvatanje cookie-ja sa pocetne stranice i ignorisanje 404 ili 500 koda
            HttpResponse<Void> initialResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());

            if (initialResponse.statusCode() == 404 || initialResponse.statusCode() == 500) {
                Map<String, List<String>> headers = initialResponse.headers().map();
                List<String> setCookieHeaders = headers.get("Set-Cookie");
                if (setCookieHeaders != null && !setCookieHeaders.isEmpty()) {
                    cookie = setCookieHeaders.get(0);

                    // Ubacivanje cookie-ja u zahtev za crumb
                    String crumbUrl = "https://query2.finance.yahoo.com/v1/test/getcrumb";

                    httpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(crumbUrl))
                            .header("Cookie", cookie)
                            .header("User-Agent", userAgent)
                            .GET()
                            .build();

                    HttpResponse<String> crumbResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                    // Check if the crumb request is successful
                    if (crumbResponse.statusCode() == 200) {
                        crumb = crumbResponse.body();
                        return true;
                    } else {
                        throw new OptionsException("Failed to retrieve crumb value. Status code: " + crumbResponse.statusCode());
                    }
                } else {
                    throw new OptionsException("No Set-Cookie header found in initial response");
                }
            } else {
                throw new OptionsException("Initial HTTP request did not return a 404||500 response");
            }
        } catch (Exception e) {
            System.out.println("Problem with getting cookie: " + e.getMessage());
//            throw new OptionsException("Problem with getting cookie: " + e.getMessage());
        }
        return false;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setCrumb(String crumb) {
        this.crumb = crumb;
    }
}



