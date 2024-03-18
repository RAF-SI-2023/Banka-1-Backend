package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ListingModel;

import javax.lang.model.element.Element;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OptionsServiceImpl implements OptionsService{
    private static String cookie = null;
    private static String crumb = null;
    @Override
    public void initOptions() {

    }

    @Override
    public List<Object> fetchOptions() {
        // ovo nije dobar url --> vrati HTML stranicu
//        https://query2.finance.yahoo.com/v7/finance/options/MSFT?crumb=U2GshuxwTFy

        if (cookie == null || crumb == null) {
            // If not cached, obtain them
            if (!getCookieAndCrumb()) {
                System.out.println("Failed to obtain cookie and crumb values");
                return null;
            }
        }

        // Now, use the cached values to make the subsequent request
        String urlWithCrumb = "https://query2.finance.yahoo.com/v7/finance/quote?symbols=TSLA&crumb=" + crumb;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithCrumb))
                .header("Cookie", cookie) // Include the cached cookie in the request
                .GET()
                .build();

        try {
            // Send the request to retrieve data with crumb value
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Print the response body
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                System.out.println("Response Body:");
                System.out.println(responseBody);

            } else {
                System.out.println("Failed to retrieve data. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        String str = fetchData("https://query2.finance.yahoo.com/v7/finance/options/MSFT?crumb="+getCrumb());
//        System.out.println(str);
//        JsonNode rootNode = objectMapper.readTree(file);
//
//        List<ListingModel> listings = new ArrayList<>();
//
//        // Iterate over each element in the JSON array
//        for (JsonNode node : rootNode) {
//            ListingModel listingModel = new ListingModel();
//            listingModel.setTicker(node.path("symbol").asText());
//            listingModel.setName(node.path("companyName").asText());
//            listingModel.setExchange(node.path("primaryExchange").asText());
//
//            listingModel.setLastRefresh((int) (System.currentTimeMillis() / 1000));
//
//            // Add the ListingModel object to the list
//            listings.add(listingModel);
//        }

        return null;
    }

    private static String getCrumb() {
//        https://query2.finance.yahoo.com/v1/test/getcrumb
        String crumb = fetchData("https://query2.finance.yahoo.com/v1/test/getcrumb");
//        System.out.println(crumb);
        return crumb;
    }

    private static String fetchData(String apiUrl) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean getCookieAndCrumb() {
        String initialUrl = "https://fc.yahoo.com";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(initialUrl))
                .GET()
                .build();

        try {
            // Dohvatanje cookie-ja sa pocetne stranice i ignorisanje 404 ili 500 koda
            HttpResponse<Void> initialResponse = client.send(request, HttpResponse.BodyHandlers.discarding());

            if (initialResponse.statusCode() == 404 || initialResponse.statusCode() == 500) {
                Map<String, List<String>> headers = initialResponse.headers().map();
                List<String> setCookieHeaders = headers.get("Set-Cookie");
                if (setCookieHeaders != null && !setCookieHeaders.isEmpty()) {
                    cookie = setCookieHeaders.get(0);

                    // Ubacivanje cookie-ja u zahtev za crumb
                    String crumbUrl = "https://query2.finance.yahoo.com/v1/test/getcrumb";

                    HttpRequest crumbRequest = HttpRequest.newBuilder()
                            .uri(URI.create(crumbUrl))
                            .header("Cookie", cookie)
                            .GET()
                            .build();

                    HttpResponse<String> crumbResponse = client.send(crumbRequest, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Crumb " + crumbResponse.body());
                    System.out.println("Cookie"+cookie);
                    // Check if the crumb request is successful
                    if (crumbResponse.statusCode() == 200) {
                        crumb = crumbResponse.body();
                        System.out.println("Cached Cookie: " + cookie);
                        System.out.println("Cached Crumb: " + crumb);
                        return true;
                    } else {
                        System.out.println("Failed to retrieve crumb value. Status code: " + crumbResponse.statusCode());
                    }
                } else {
                    System.out.println("No Set-Cookie header found in initial response");
                }
            } else {
                System.out.println("Initial HTTP request did not return a 404||500 response");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
     }


}



