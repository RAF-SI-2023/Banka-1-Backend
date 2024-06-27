package zews.otc_testing.entity.offer;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MyOffer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myOfferId;
    private String ticker;
    private Integer amount;
    private Double price;
    private OfferStatus offerStatus;

}
