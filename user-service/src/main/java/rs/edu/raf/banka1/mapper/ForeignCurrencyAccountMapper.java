package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.UUID;

@Component
public class ForeignCurrencyAccountMapper {

    public ForeignCurrencyAccountResponse foreignCurrencyAccountToForeignCurrencyAccountResponse(ForeignCurrencyAccount foreignCurrencyAccount) {
        ForeignCurrencyAccountResponse foreignCurrencyAccountResponse = new ForeignCurrencyAccountResponse();
        foreignCurrencyAccountResponse.setOwnerId(foreignCurrencyAccount.getOwnerId());
        foreignCurrencyAccountResponse.setAssignedAgentId(foreignCurrencyAccount.getCreatedByAgentId());
        foreignCurrencyAccountResponse.setCurrency(foreignCurrencyAccount.getCurrency());
        foreignCurrencyAccountResponse.setTypeOfAccount(foreignCurrencyAccount.getTypeOfAccount());
        foreignCurrencyAccountResponse.setSubtypeOfAccount(foreignCurrencyAccount.getSubtypeOfAccount());
        foreignCurrencyAccountResponse.setAccountMaintenance(foreignCurrencyAccount.getAccountMaintenance());
        foreignCurrencyAccountResponse.setDefaultCurrency(foreignCurrencyAccount.getDefaultCurrency());
        foreignCurrencyAccountResponse.setAllowedCurrencies(foreignCurrencyAccount.getAllowedCurrencies());
        return foreignCurrencyAccountResponse;
    }

    public ForeignCurrencyAccount createForeignCurrencyAccountRequestToForeignCurrencyAccount(ForeignCurrencyAccountRequest foreignCurrencyAccountRequest) {
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        foreignCurrencyAccount.setOwnerId(foreignCurrencyAccountRequest.getOwnerId());
        foreignCurrencyAccount.setCreatedByAgentId(foreignCurrencyAccountRequest.getAssignedAgentId());
        foreignCurrencyAccount.setCurrency(foreignCurrencyAccountRequest.getCurrency());
        foreignCurrencyAccount.setTypeOfAccount(foreignCurrencyAccountRequest.getTypeOfAccount());
        foreignCurrencyAccount.setSubtypeOfAccount(foreignCurrencyAccountRequest.getSubtypeOfAccount());
        foreignCurrencyAccount.setAccountMaintenance(foreignCurrencyAccountRequest.getAccountMaintenance());
        foreignCurrencyAccount.setDefaultCurrency(foreignCurrencyAccountRequest.getDefaultCurrency());
        foreignCurrencyAccount.setAllowedCurrencies(foreignCurrencyAccountRequest.getAllowedCurrencies());
        foreignCurrencyAccount.setAccountNumber(UUID.randomUUID().toString());
        foreignCurrencyAccount.setBalance(1000.0);
        foreignCurrencyAccount.setAccountStatus("ACTIVE");
        foreignCurrencyAccount.setCreationDate((int) System.currentTimeMillis());
        foreignCurrencyAccount.setExpirationDate((int) System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);

        return foreignCurrencyAccount;
    }
}
