package rs.edu.raf.banka1.utils;

import java.io.File;
import java.util.List;

public class Constants {
    public static String listingsFilePath = "./market-service/src/main/resources/listings.json";
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
}
