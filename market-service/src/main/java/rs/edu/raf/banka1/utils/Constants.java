package rs.edu.raf.banka1.utils;


import java.util.List;

public class Constants {
    public static final String listingsFilePath = "market-service/src/main/resources/listings.json";
    public static final String businessHoursFilePath = "market-service/src/main/resources/working_hours_and_holidays_for_exchanges.json";
    public static final String micCsvFilePath = "market-service/src/main/resources/ISO10383_MIC.csv";
    public static final String countryTimezoneOffsetsFilePath = "market-service/src/main/resources/country_timezone_offsets.json";
    public static String optionsFilePath = "market-service/src/main/resources/options.json";
    public static final List<String> sectors = List.of("Technology");
    public static List<String> tickersForTestingOptions = List.of("APPL", "ORCL", "MSFT", "VXX");
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

    public static String currencyFilePath = "market-service/src/main/resources/physical_currency_list.csv";
}
