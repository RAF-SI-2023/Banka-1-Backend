package rs.edu.raf.banka1.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.List;

public class Constants {
    public static final String listingsFilePath = getAbsoluteFilePath(
            "listings.json");
    public static final String businessHoursFilePath = getAbsoluteFilePath(
            "working_hours_and_holidays_for_exchanges.json");
    public static final String micCsvFilePath = getAbsoluteFilePath(
            "ISO10383_MIC.csv");
    public static final String countryTimezoneOffsetsFilePath = getAbsoluteFilePath(
            "country_timezone_offsets.json");
    public static final List<String> sectors = List.of(
            "Technology", "Electronic Technology", "Health Technology", "Health Services", "Finance", "Energy");
    public static final int maxStockListings = 20;
    public static final int maxStockListingsHistory = 10;
    public static final int maxFutures = 10;
    public static final int maxFutureHistories = 20;
    public static String optionsFilePath = getAbsoluteFilePath(
            "options.json");
    //    public static final List<String> sectors = List.of("Technology");
    public static List<String> tickersForTestingOptions = List.of("AAPL", "ORCL", "MSFT", "VXX");
    public static final int maxListings = 700;
    public static final Integer BEARER_PREFIX_SIZE = 7;
    public static final List<String> ListingsToIgnore = List.of(
            "ATLEF",
            "NTXNF",
            "HUTMF",
            "AMPO",
            "RMRMF",
            "IO",
            "BCOR",
            "TPNHY",
            "COSDF",
            "ATMQF",
            "FLUXF");

    public static String currencyFilePath = getAbsoluteFilePath(
            "physical_currency_list.csv");


    public static String getAbsoluteFilePath(String relativePath) {
        try {
            Resource resource = new ClassPathResource(relativePath);
            if (resource.exists()) {
                return resource.getURL().getPath().replaceAll("%20", " ");
            } else {
                Logger.warn("Resource does not exist: " + relativePath);
            }
        } catch (IOException e) {
            Logger.error(e, "Cannot load resource whose relative path is " + relativePath);
        }
        return null;
    }
}
