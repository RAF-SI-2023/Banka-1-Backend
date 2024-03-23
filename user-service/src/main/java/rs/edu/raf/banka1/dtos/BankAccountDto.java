package rs.edu.raf.banka1.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountDto {
//    it will be assigned based on from what table it was taken
    private String accountType;
    private String accountNumber;
    private String accountStatus;
    private String currency;
    private Double balance;
    private Double availableBalance;
}
