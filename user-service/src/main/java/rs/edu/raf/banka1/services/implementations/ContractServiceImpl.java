package rs.edu.raf.banka1.services.implementations;

import io.swagger.models.Contact;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.dtos.ContractDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.ContractNotFoundByIdException;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.mapper.ContractMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.ContractRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.ContractService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.utils.Constants;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final BankAccountService bankAccountService;
    private final ContractRepository contractRepository;
    private final CapitalService capitalService;

    @Override
    public ContractDto createContract(ContractCreateDto contractCreateDto, User buyer) {

        String referenceNumber = (contractRepository.count() + 1) + "/" + Year.now().getValue();

        BankAccount buyerAccount = null;
        BankAccount sellerAccount = bankAccountService.getBankAccountByNumber(contractCreateDto.getBankAccountNumber());;

        if(buyer.getCompany() == null) {
            buyerAccount = bankAccountService.getBankAccountByCustomerAndCurrencyCode(buyer.getUserId(), Constants.DEFAULT_CURRENCY);
        } else {
            buyerAccount = bankAccountService.getBankAccountByCompanyAndCurrencyCode(buyer.getCompany().getId(), Constants.DEFAULT_CURRENCY);
        }

        Capital sellerCapital = capitalService.getCapitalByListingIdAndTypeAndBankAccount(contractCreateDto.getListingId(), contractCreateDto.getListingType(), sellerAccount);

        if(contractCreateDto.getAmountToBuy() <= 0 || contractCreateDto.getAmountToBuy() > sellerCapital.getPublicTotal()) {
            throw new InvalidCapitalAmountException(contractCreateDto.getAmountToBuy());
        }

        Contract contract = new Contract();
        contract.setBuyer(buyerAccount);
        contract.setSeller(sellerAccount);
        contract.setBankApproval(false);
        contract.setSellerApproval(false);
        contract.setCreationDate(Instant.now().toEpochMilli());
        contract.setReferenceNumber(referenceNumber);
        contract.setTicker(contractCreateDto.getTicker());
        contract.setAmount(contractCreateDto.getAmountToBuy());
        contract.setPrice(contractCreateDto.getOfferPrice());
        contract.setListingId(contractCreateDto.getListingId());
        contract.setListingType(contractCreateDto.getListingType());

        contractRepository.save(contract);

        return contractMapper.contractToContractDto(contract);
    }

    @Override
    public Boolean denyContract(Long contractId, String comment) {
        contractRepository.updateCommentById(comment, contractId);
        return true;
    }

    @Transactional
    @Override
    public Boolean acceptContract(Long contractId) {
        contractRepository.acceptContract(contractId);
        finalizeContract(contractId);
        return true;
    }

    @Transactional
    @Override
    public Boolean approveContract(Long contractId) {
        contractRepository.approveContract(contractId);
        finalizeContract(contractId);
        return true;
    }

    @Override
    public List<ContractDto> getAllContractsSupervisor(Employee currentAuth) {
        List<Contract> contracts = contractRepository.findAll();

        List<ContractDto> contractDtos = new ArrayList<>();

        contracts.forEach((Contract contract) -> {
            ContractDto contractDto = contractMapper.contractToContractDto(contract);
            contractDtos.add(contractDto);
        });

        return contractDtos;
    }

    @Override
    public List<ContractDto> getAllContractsCustomer(Customer currentAuth) {
        BankAccount bankAccount = null;
        if(currentAuth.getCompany() == null) {
            bankAccount = bankAccountService.getBankAccountByCustomerAndCurrencyCode(currentAuth.getUserId(), Constants.DEFAULT_CURRENCY);
        } else {
            bankAccount = bankAccountService.getBankAccountByCompanyAndCurrencyCode(currentAuth.getCompany().getId(), Constants.DEFAULT_CURRENCY);
        }
        List<Contract> contracts = contractRepository.findAllCustomerContracts(bankAccount.getAccountNumber());

        List<ContractDto> contractDtos = new ArrayList<>();

        contracts.forEach((Contract contract) -> {
            ContractDto contractDto = contractMapper.contractToContractDto(contract);
            contractDtos.add(contractDto);
        });

        return contractDtos;
    }


    private void finalizeContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId).orElseThrow(() -> new ContractNotFoundByIdException(contractId));
        if(!contract.getBankApproval() || !contract.getSellerApproval()) return;

        //Transfer capital
        capitalService.removeFromPublicCapital(contract.getListingId(), contract.getListingType(), contract.getSeller(), contract.getAmount());
        capitalService.removeBalance(contract.getListingId(), contract.getListingType(), contract.getSeller(), contract.getAmount());
        capitalService.addBalance(contract.getListingId(), contract.getListingType(), contract.getBuyer(), contract.getAmount());

        //Transfer funds
        bankAccountService.removeBalance(contract.getBuyer(), contract.getPrice());
        bankAccountService.addBalance(contract.getSeller(), contract.getPrice());
    }

}
