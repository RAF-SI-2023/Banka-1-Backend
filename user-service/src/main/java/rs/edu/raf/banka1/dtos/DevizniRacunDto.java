package rs.edu.raf.banka1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.VrstaRacuna;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DevizniRacunDto {

    private String ownerId;
    private Long assignedAgentId;
    private String currency;
    private VrstaRacuna vrstaRacuna;
    private Integer interestRatePercentage;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
//    private List<String> allowedCurrencies;
}
