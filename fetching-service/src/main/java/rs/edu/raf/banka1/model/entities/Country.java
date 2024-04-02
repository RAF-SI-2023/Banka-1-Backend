package rs.edu.raf.banka1.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
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

//    @OneToMany(mappedBy = "country")
//    private Set<Holiday> holidays = new HashSet<>();

//    @OneToMany(mappedBy = "country")
//    private List<Exchange> exchanges = new ArrayList<>();
}
