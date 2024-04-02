package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingHistoryDto {
    private Long listingHistoryId;
    private String ticker;
    private long date;
    private Double price;
    private Double high;
    private Double low;
    private Double changed;
    private Integer volume;
}
