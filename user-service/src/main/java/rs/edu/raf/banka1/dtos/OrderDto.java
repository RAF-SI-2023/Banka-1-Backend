package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.OrderType;

@Getter
@Setter
@ToString
public class OrderDto {
    private Long orderId;
    private Long listingId;
    private ListingType listingType;
    private EmployeeDto owner;
    private OrderType orderType;
    private OrderStatus status;
    private Long contractSize;
    private Long processedNumber;
    private Double limitValue;
    private Double stopValue;
    private Double fee;
    private Double price;
    private Boolean allOrNone;
    private Long updatedAt;
    private EmployeeDto approvedBy;
}
