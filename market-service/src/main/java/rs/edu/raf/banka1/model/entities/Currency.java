package rs.edu.raf.banka1.model.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String currencyName;

    @Column(unique = true)
    private String currencyCode;

    @Column
    private String currencySymbol;

    @Column
    private String polity;

    @OneToMany(mappedBy = "currency")
    private List<Inflation> inflations;
}
