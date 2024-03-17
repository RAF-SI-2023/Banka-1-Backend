package rs.edu.raf.banka1.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constants {
    public static String listingsFilePath = "src/main/resources/listings.json";
    public static List<String> sectors = List.of("Technology");
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

    public static String currencyFilePath = "src/main/resources/physical_currency_list.csv";
}
