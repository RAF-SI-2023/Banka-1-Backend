package rs.edu.raf.banka1.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InflationDto {

    private Integer year;

    private Float inflationRate;
}
