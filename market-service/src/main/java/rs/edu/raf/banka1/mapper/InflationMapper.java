package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.dtos.InflationDto;
import rs.edu.raf.banka1.model.entities.Inflation;

@Component
public class InflationMapper {

    public InflationDto inflationToInflationDto(Inflation inflation) {
        InflationDto inflationDto = new InflationDto();
        inflationDto.setYear(inflation.getYear());
        inflationDto.setInflationRate(inflation.getInflationRate());
        return inflationDto;
    }
}
