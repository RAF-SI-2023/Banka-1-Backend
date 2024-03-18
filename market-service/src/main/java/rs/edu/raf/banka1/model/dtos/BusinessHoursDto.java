package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BusinessHoursDto {
    private String open;
    private String close;
    private List<String> holidays;
}
