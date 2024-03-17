package rs.edu.raf.banka1.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ISOCode;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "country")
    private List<Holiday> holidays;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "country")
    private List<Exchange> exchanges;

}
