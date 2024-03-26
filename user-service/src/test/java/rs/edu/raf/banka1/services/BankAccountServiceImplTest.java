package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    public ForeignCurrencyAccountRequest createForeignCurrencyAccountRequest() {
        ForeignCurrencyAccountRequest request = new ForeignCurrencyAccountRequest();
        request.setOwnerId(2L);
        request.setCreatedByAgentId(1L);
//        request.setTypeOfAccount("DEVIZNI");
        request.setCurrency("USD");
        request.setSubtypeOfAccount("LICNI");
        request.setAccountMaintenance(10.0);
        request.setDefaultCurrency(true);
        request.setAllowedCurrencies(List.of("USD", "EUR", "CHF"));
        return request;
    }
}
