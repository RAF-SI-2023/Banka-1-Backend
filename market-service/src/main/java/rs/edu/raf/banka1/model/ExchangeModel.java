package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ExchangeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exchangeId;
    private String exchangeName;
    private String exchangeAcronym;
    private String micCode;
    private String country;
    private String currency;
    private String timeZone;

    private String openTime;
    private String closeTime;
}

