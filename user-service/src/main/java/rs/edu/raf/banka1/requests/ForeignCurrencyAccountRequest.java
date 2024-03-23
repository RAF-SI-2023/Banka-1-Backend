package rs.edu.raf.banka1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ForeignCurrencyAccountRequest {

    private Long ownerId;
    private Long createdByAgentId;
    private String currency;
    private String subtypeOfAccount;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
    private List<String> allowedCurrencies;
}
