package rs.edu.raf.banka1.utils;

import java.util.List;

public class Constants {
    public static String listingsFilePath = "src/main/resources/listings.json";
    public static String optionsFilePath = "src/main/resources/options.json";
    public static List<String> sectors = List.of("Technology");
    public static List<String> tickersForTestingOptions = List.of("APPL", "ORCL", "MSFT", "VXX");
    public static int maxListings = 700;
    public static List<String> ListingsToIgnore = List.of(
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
}
