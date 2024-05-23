package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;

@Getter
@Setter
public class ContractDto {
    private Long contractId;
    private String buyerAccountNumber;
    private String sellerAccountNumber;
    private Boolean bankApproval;
    private Boolean sellerApproval;
    private String comment;
    private Long creationDate;
    private Long realizationDate;
    private String referenceNumber;
    private String ticker;
    private Long listingId;
    private Double amount;
    private Double price;
    private ListingStockDto listingStockDto;
    private ListingForexDto listingForexDto;
    private ListingFutureDto listingFutureDto;
}
