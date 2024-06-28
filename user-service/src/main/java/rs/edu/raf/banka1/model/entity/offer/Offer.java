package rs.edu.raf.banka1.model.entity.offer;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Offer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;
    private String ticker;
    private Integer amount;
    private Double price;
    private Long idBank; //id koji ce stizati od banke
    @Enumerated(EnumType.STRING)
    private OfferStatus offerStatus;
}
