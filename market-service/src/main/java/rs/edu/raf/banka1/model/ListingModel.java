package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ListingModel {
    @Id
    private String ticker;
    private String name;
    private String exchange;
    private LocalDateTime lastRefresh;
    private double price;
    private double ask;
    private double bid;
    private double changed;
    private int volume;

}
