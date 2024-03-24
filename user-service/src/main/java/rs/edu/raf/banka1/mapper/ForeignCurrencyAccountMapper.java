package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.Random;
import java.util.UUID;

@Component
public class ForeignCurrencyAccountMapper {

    public ForeignCurrencyAccountResponse foreignCurrencyAccountToForeignCurrencyAccountResponse(ForeignCurrencyAccount foreignCurrencyAccount) {
        ForeignCurrencyAccountResponse foreignCurrencyAccountResponse = new ForeignCurrencyAccountResponse();
        foreignCurrencyAccountResponse.setOwnerId(foreignCurrencyAccount.getOwnerId());
        foreignCurrencyAccountResponse.setCreatedByAgentId(foreignCurrencyAccount.getCreatedByAgentId());
        foreignCurrencyAccountResponse.setCurrency(foreignCurrencyAccount.getCurrency());
        foreignCurrencyAccountResponse.setSubtypeOfAccount(foreignCurrencyAccount.getSubtypeOfAccount());
        foreignCurrencyAccountResponse.setAccountMaintenance(foreignCurrencyAccount.getAccountMaintenance());
        foreignCurrencyAccountResponse.setDefaultCurrency(foreignCurrencyAccount.getDefaultCurrency());
        return foreignCurrencyAccountResponse;
    }

    public ForeignCurrencyAccount createForeignCurrencyAccountRequestToForeignCurrencyAccount(ForeignCurrencyAccountRequest foreignCurrencyAccountRequest) {
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        foreignCurrencyAccount.setOwnerId(foreignCurrencyAccountRequest.getOwnerId());
        foreignCurrencyAccount.setCreatedByAgentId(foreignCurrencyAccountRequest.getCreatedByAgentId());
        foreignCurrencyAccount.setCurrency(foreignCurrencyAccountRequest.getCurrency());
        foreignCurrencyAccount.setSubtypeOfAccount(foreignCurrencyAccountRequest.getSubtypeOfAccount());
        foreignCurrencyAccount.setAccountMaintenance(foreignCurrencyAccountRequest.getAccountMaintenance());
        foreignCurrencyAccount.setDefaultCurrency(foreignCurrencyAccountRequest.getDefaultCurrency());
        foreignCurrencyAccount.setAccountNumber(UUID.randomUUID().toString());
        foreignCurrencyAccount.setAccountNumber(generateAccountNumber());
        foreignCurrencyAccount.setBalance(1000.0);
        foreignCurrencyAccount.setAccountStatus("ACTIVE");
        foreignCurrencyAccount.setCreationDate((int) System.currentTimeMillis());
        foreignCurrencyAccount.setExpirationDate((int) System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);
        return foreignCurrencyAccount;
    }

    public String generateAccountNumber() {
        return "1234567" + generateRandomNumber(9) + "01";
    }

    public String generateRandomNumber(int numOfDigits) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numOfDigits; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
