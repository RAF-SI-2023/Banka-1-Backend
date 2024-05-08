package rs.edu.raf.banka1.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.FetchType;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Country implements Serializable {
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
