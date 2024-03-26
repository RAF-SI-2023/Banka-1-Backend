package rs.edu.raf.banka1.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ForeignCurrencyAccountResponse {

    private Long ownerId;
    private Long createdByAgentId;
    private String currency;
    private String subtypeOfAccount;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
    private List<String> allowedCurrencies;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ForeignCurrencyAccountResponse that = (ForeignCurrencyAccountResponse) o;
        return Objects.equals(ownerId, that.ownerId)
                && Objects.equals(createdByAgentId, that.createdByAgentId)
                && Objects.equals(currency, that.currency)
                && Objects.equals(subtypeOfAccount, that.subtypeOfAccount)
                && Objects.equals(accountMaintenance, that.accountMaintenance)
                && Objects.equals(defaultCurrency, that.defaultCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, createdByAgentId, currency, subtypeOfAccount, accountMaintenance, defaultCurrency);
    }
}
