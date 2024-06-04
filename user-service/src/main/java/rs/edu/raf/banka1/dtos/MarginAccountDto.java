package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;

@Getter
@Setter
public class MarginAccountDto {
    private Long id;
//    private BankAccount customer;
    private Currency currency;
    private ListingType listingType;
    private Double balance;
    private Double loanValue;
    private Double maintenanceMargin;
    private int marginCall;
}
