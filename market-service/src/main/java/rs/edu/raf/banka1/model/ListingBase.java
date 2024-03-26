package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.entities.Exchange;

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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "exchange_id", referencedColumnName = "id")
    private Exchange exchange;

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
