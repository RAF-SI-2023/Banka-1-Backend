package rs.edu.raf.banka1.utils;

import rs.edu.raf.banka1.model.Permission;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    public static final String ALL_POSITIONS = "All";
    public static final Integer BEARER_PREFIX_SIZE = 7;
    public static final Long JWT_EXPIRATION_LENGTH = 1000L * 60 * 60 * 10;
    public static final Long SINGLE_USE_CODE_EXPIRATION_LENGTH = 1000L * 60 * 5;
    public static final String AGENT = "agent";
    public static final String SUPERVIZOR = "supervizor";
    public static final String ADMIN = "admin";

    public static final String DEFAULT_CURRENCY = "RSD";
    public static final float MARGIN_RATE = 0.6f;
    public static final float MARGIN_INTEREST_RATE = 0.05f;
    public static final float MAINTENANCE_MARGIN_RATE = 0.25f;

    public static final List<String> allPermissions = Arrays.asList(
            "addUser", "modifyUser", "deleteUser", "readUser",
            "manageLoans", "manageLoanRequests", "modifyCustomer",
            "manageOrderRequests", "manageMargins");

    public static final Map<String, List<String>> userPermissions = new HashMap<String, List<String>>(){{
        put(AGENT, Arrays.asList("addUser", "modifyUser", "deleteUser", "readUser",
                "manageLoans", "manageLoanRequests", "modifyCustomer"));
        put(SUPERVIZOR, allPermissions);
        put(ADMIN, allPermissions);
    }};
}
