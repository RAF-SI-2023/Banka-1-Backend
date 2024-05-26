package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
// you should add entry at the end of the day
public class ListingHistory  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listingHistoryId;
    private String ticker;
    private long date;
    private Double price;
    private Double high;
    private Double low;
    private Double changed;
    private Integer volume;
}
