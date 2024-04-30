package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractDto {
    private Long contractId;
    private Long buyerId;
    private Long sellerId;
    private Boolean bankApproval;
    private Boolean sellerApproval;
    private String comment;
    private Long creationDate;
    private Long realizationDate;
    private String referenceNumber;
    private String ticker;
    private Double amount;
    private Double price;
}
