package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.dtos.ContractDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.model.Contract;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.User;

@Component
public class ContractMapper {
    public Contract contractCreateDtoToContract(ContractCreateDto contractCreateDto, User buyer) {
        Contract contract = new Contract();
//        contract.setAmount(contractCreateDto.getAmountToBuy());
        return contract;
    }

    public ContractDto contractToContractDto(Contract contract) {
        ContractDto contractDto = new ContractDto();

        contractDto.setContractId(contract.getId());
        contractDto.setBuyerAccountNumber(contract.getBuyer().getAccountNumber());
        contractDto.setSellerAccountNumber(contract.getSeller().getAccountNumber());
        contractDto.setBankApproval(contract.getBankApproval());
        contractDto.setSellerApproval(contract.getSellerApproval());
        contractDto.setComment(contract.getComment());
        contractDto.setCreationDate(contract.getCreationDate());
        contractDto.setRealizationDate(contract.getRealizationDate());
        contractDto.setReferenceNumber(contract.getReferenceNumber());
        contractDto.setTicker(contract.getTicker());
        contractDto.setListingId(contract.getListingId());
        contractDto.setAmount(contract.getAmount());
        contractDto.setPrice(contract.getPrice());

        return contractDto;
    }
}
