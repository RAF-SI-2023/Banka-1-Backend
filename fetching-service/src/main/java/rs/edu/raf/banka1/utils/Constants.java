package rs.edu.raf.banka1.utils;

import java.util.List;

public class Constants {
    public static final String listingsFilePath =
            "listings.json";
    public static final String businessHoursFilePath =
            "working_hours_and_holidays_for_exchanges.json";
    public static final String micCsvFilePath =
            "ISO10383_MIC.csv";
    public static final String countryTimezoneOffsetsFilePath =
            "country_timezone_offsets.json";
    public static final List<String> sectors = List.of(
            "Technology", "Electronic Technology", "Health Technology", "Health Services", "Finance", "Energy");
    public static final int maxStockListings = 10;
    public static final int maxStockListingsHistory = 10;
    public static final int maxFutures = 10;
    public static final int maxFutureHistories = 20;
    public static String optionsFilePath =
            "options.json";
    //    public static final List<String> sectors = List.of("Technology");
    public static List<String> tickersForTestingOptions = List.of("AAPL", "ORCL", "MSFT", "VXX");
    public static final int maxListings = 700;
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

    public static String currencyFilePath =
            "physical_currency_list.csv";
}
