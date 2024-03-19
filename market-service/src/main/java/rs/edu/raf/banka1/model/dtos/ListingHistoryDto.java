package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingHistoryDto {
    private Long id;
    private Long listingId;
    private String ticker;
    private Integer date;
    private Double price;
    private Double ask;
    private Double bid;
    private Double changed;
    private Integer volume;
}
