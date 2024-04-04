package rs.edu.raf.banka1.model.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
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
