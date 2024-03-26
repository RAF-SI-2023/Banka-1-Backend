package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class ListingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listingId;

    @Column
    private String listingType;

    @Column
    private String ticker;

    @Column
    private String name;

    @Column
    private String exchangeName;

    @Column
    private Integer lastRefresh;

    @Column
    private Double price;

    @Column
    private Double high;

    @Column
    private Double low;

    // change is a reserved keyword in SQL
    @Column(name = "price_change")
    private Double priceChange;

    @Column
    private Integer volume;

}
