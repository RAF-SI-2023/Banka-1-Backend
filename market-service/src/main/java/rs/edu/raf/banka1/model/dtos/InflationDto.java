package rs.edu.raf.banka1.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InflationDto implements Serializable {

    private Integer year;

    private Float inflationRate;
}
