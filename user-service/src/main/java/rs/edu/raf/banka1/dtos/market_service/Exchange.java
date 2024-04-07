package rs.edu.raf.banka1.dtos.market_service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Exchange {
    private Long id;
    private String exchangeName;
    private String exchangeAcronym;
    private String micCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    private String currency; //todo: should this be FK to Currency table, also where to get this info
}
