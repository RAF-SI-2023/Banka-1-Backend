package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewLimitDto {
    private Double limit;
    private Boolean approvalRequired;
    private Long userId;
}