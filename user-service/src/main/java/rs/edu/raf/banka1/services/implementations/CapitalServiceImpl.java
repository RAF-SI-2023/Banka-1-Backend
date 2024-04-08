package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.services.CapitalService;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Getter
@Setter
public class CapitalServiceImpl implements CapitalService {

    private BankAccountRepository bankAccountRepository;
    private CapitalRepository capitalRepository;
    private CapitalMapper capitalMapper;
    public CapitalServiceImpl(BankAccountRepository bankAccountRepository,
                              CapitalRepository capitalRepository,
                              CapitalMapper capitalMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.capitalRepository = capitalRepository;
        this.capitalMapper = capitalMapper;
    }

    @Override
    public Capital createCapitalForBankAccount(BankAccount bankAccount, Currency currency, Double total, Double reserved) {
        Capital capital = new Capital();
        capital.setBankAccount(bankAccount);
        capital.setCurrency(currency);
        capital.setTotal(total);
        capital.setReserved(reserved);

        return capital;
    }

    @Override
    public Capital createCapitalForListing(ListingType listingType, Long listingId, Double total, Double reserved) {
        Capital capital = new Capital();
        capital.setListingType(listingType);
        capital.setListingId(listingId);
        capital.setTotal(total);
        capital.setReserved(reserved);

        return capital;
    }

    @Override
    public List<CapitalDto> getCapitalForListing(String accountNumber, ListingType listingType) {
        BankAccount bankAccount = this.bankAccountRepository.findBankAccountByAccountNumber(accountNumber).orElseThrow(BankAccountNotFoundException::new);
        return this.capitalRepository.getCapitalsByBankAccountAndListingType(bankAccount, listingType).stream().map(capitalMapper::capitalToCapitalDto).collect(Collectors.toList());
    }
}
