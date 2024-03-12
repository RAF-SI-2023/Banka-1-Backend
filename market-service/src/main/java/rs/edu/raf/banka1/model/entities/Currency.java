package rs.edu.raf.banka1.model.entities;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Currency{

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
}
