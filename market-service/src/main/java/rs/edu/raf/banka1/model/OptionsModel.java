package rs.edu.raf.banka1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.enums.OptionType;

@Entity
@Getter
@Setter
public class OptionsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String ticker;
    @Column
    private OptionType optionType;
    @Column
    private double strikePrice;
    @Column
    private String currency;
    @Column
    private double impliedVolatility;
    @Column
    private int openInterest;
    @Column
    private long expirationDate;
}
