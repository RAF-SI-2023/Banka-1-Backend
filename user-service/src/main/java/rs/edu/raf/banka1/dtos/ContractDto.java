package rs.edu.raf.banka1.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ContractDto {
    private Long contractId;
    private String buyerAccountNumber;
    private String sellerAccountNumber;
    private Boolean bankApproval;
    private Boolean sellerApproval;
    private String comment;
    @EqualsAndHashCode.Exclude
    private Long creationDate;
    @EqualsAndHashCode.Exclude
    private Long realizationDate;
    private String referenceNumber;
    private String ticker;
    private Long listingId;
    private Double amount;
    private Double price;
}
