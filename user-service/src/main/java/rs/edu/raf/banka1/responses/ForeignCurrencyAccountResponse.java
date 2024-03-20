package rs.edu.raf.banka1.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ForeignCurrencyAccountResponse {

    private Long ownerId;
    private Long createdByAgentId;
    private String currency;
    private String typeOfAccount;
    private String subtypeOfAccount;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
    private List<String> allowedCurrencies;
}
