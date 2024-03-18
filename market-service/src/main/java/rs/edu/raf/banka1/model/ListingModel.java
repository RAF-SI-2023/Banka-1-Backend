package rs.edu.raf.banka1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class ListingModel {
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

}
