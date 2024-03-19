package rs.edu.raf.banka1.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ISOCode;
    private int timezoneOffset;

    @Temporal(TemporalType.TIME)
    private Date openTime;

    @Temporal(TemporalType.TIME)
    private Date closeTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "country")
    private Set<Holiday> holidays = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "country")
    private List<Exchange> exchanges = new ArrayList<>();
}
