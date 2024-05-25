package rs.edu.raf.banka1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TransfersReportDto {

    public TransfersReportDto() {
        transfers = new ArrayList<>();
    }

    private List<TransferDto> transfers;
    private Double profit;
}
