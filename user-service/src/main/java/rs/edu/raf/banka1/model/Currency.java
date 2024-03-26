package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String currencyName;
    @Column
    private String currencyCode;
    @Column
    private String currencySymbol;
    @Column
    private String country;
    @Column
    private String currencyDesc;
    @Column
    private Boolean active;

}