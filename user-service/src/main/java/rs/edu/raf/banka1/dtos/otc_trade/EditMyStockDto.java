package rs.edu.raf.banka1.dtos.otc_trade;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditMyStockDto {
    private String ticker;
    private Integer publicAmount;
    private Double price;
}
