package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ListingModel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class OptionsServiceImpl implements OptionsService{

    @Override
    public void initOptions() {

    }

    @Override
    public List<Object> fetchOptions() {
        // ovo nije dobar url --> vrati HTML stranicu
//        https://query2.finance.yahoo.com/v7/finance/options/MSFT?crumb=U2GshuxwTFy

//        String str = fetchData("https://query2.finance.yahoo.com/v7/finance/options/MSFT?crumb="+getCrumb());
        System.out.println(getCrumb());
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
}
