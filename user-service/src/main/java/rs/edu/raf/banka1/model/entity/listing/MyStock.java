package rs.edu.raf.banka1.model.entity.listing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyStock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myStockId;
    private String ticker;
    private Long userId;
    private Long companyId;
    private Integer amount;
    private String currencyMark;
    private Integer privateAmount;
    // getAll ali samo public za korisnike  i kompanije > 0
    // mora da se razlikuje i da li je kompanija ili korisnik ulogovan
    // na osnovu toga se vracaju public stockovi
    private Integer publicAmount;
    @JsonIgnore
    private Double minimumPrice;
}
