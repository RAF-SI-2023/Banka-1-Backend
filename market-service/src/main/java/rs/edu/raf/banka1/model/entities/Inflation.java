package rs.edu.raf.banka1.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "inflations", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Inflation  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;
    private Float inflationRate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    public Inflation(Integer year, Float inflationRate, Currency currency) {
        this.year = year;
        this.inflationRate = inflationRate;
        this.currency = currency;
    }
}
