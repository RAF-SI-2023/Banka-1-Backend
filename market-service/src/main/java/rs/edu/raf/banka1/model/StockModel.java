package rs.edu.raf.banka1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockModel {
    @Id
    @JsonProperty("symbol")
    private String ticker;

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

    @JsonProperty("shares")
    private int outstandingShares;

    @JsonProperty("yield")
    private double dividendYield;

    @JsonProperty("marketCap")
    private double marketCap;

    @JsonProperty("contractSize")
    private int contractSize;

    @JsonProperty("maintenanceMargin")
    private int maintenanceMargin;
}
