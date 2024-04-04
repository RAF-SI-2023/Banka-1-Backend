package rs.edu.raf.banka1.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingBaseDto {
    private Double price;
    private Double high;
    private Double low;
    private Double priceChange;
    private Integer volume;
}
