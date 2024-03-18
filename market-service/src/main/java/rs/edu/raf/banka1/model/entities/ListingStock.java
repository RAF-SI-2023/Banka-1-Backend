package rs.edu.raf.banka1.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListingStock {
    @Id
    @JsonProperty("symbol")
    private String ticker;
    @JsonProperty("outstandingShares")
    private Integer outstandingShares;
    @JsonProperty("dividendYield")
    private Double dividendYield;

    @JsonProperty("companyName")
    private String name;

    @JsonProperty("primaryExchange")
    private String exchange;

    @JsonIgnore
    private long lastRefresh;

    @JsonProperty("latestPrice")
    private double price;

    @JsonProperty("high")
    private double ask;

    @JsonProperty("low")
    private double bid;

    @JsonProperty("change")
    private double changed;

    @JsonProperty("volume")
    private int volume;


}
