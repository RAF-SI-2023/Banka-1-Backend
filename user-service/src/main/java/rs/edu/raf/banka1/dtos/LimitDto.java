package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitDto {
    private String email;
    private Double limit;
    private Double usedLimit;
    private Boolean approvalRequired;
}
