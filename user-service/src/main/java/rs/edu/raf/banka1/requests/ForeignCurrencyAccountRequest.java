package rs.edu.raf.banka1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.SubtypeOfAccount;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ForeignCurrencyAccountRequest {

    private Long ownerId;
    private Long assignedAgentId;
    private String currency;
    private String typeOfAccount;
    private SubtypeOfAccount subtypeOfAccount;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
    private List<String> allowedCurrencies;
}
