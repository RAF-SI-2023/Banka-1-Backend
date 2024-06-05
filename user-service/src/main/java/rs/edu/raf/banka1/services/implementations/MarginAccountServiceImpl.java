package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.exceptions.MarginAccountNotFoundException;
import rs.edu.raf.banka1.mapper.MarginAccountMapper;
import rs.edu.raf.banka1.margincalljob.MarginCallMidnightJob;
import rs.edu.raf.banka1.margincalljob.MarginCallMidnightTrigger;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarginAccount;
import org.tinylog.Logger;
import rs.edu.raf.banka1.dtos.MarginAccountDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.MarginAccountRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.MarginAccountService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarginAccountServiceImpl implements MarginAccountService {

    private final MarginAccountRepository marginAccountRepository;
    private final BankAccountService bankAccountService;
    private final MarginAccountMapper marginAccountMapper;
    private final EmailService emailService;
    private final TaskScheduler taskScheduler;

    @Override
    public MarginAccount getMarginAccount(Long id, ListingType listingType, String currencyCode) {
        return marginAccountRepository.findByCustomer_IdAndListingTypeAndCurrency_CurrencyCode(id, listingType, currencyCode)
                .orElseThrow(() -> new MarginAccountNotFoundException(id, listingType, currencyCode));
    }

    @Override
    public List<MarginAccountDto> getAllMarginAccounts() {
        return marginAccountRepository.findAll().stream()
                .map(marginAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarginAccountDto> getMyMargin(Customer customer) {
        List<BankAccount> bankAccounts = customer.getAccountIds();
        return marginAccountRepository.findAll()
                .stream()
                .filter(marginAccount -> {
                    for (BankAccount ba : bankAccounts) {
                        if (ba.getId().equals(marginAccount.getCustomer().getId())) {
                            return true;
                        }
                    }
                    return false;
                }).map(marginAccountMapper::toDto).collect(Collectors.toList());
    }
    @Override
    public Boolean createMarginAccount(MarginAccountCreateDto marginAccountCreateDto) {
        // samo proveri da li vec postoji margin account sa
        // prosledjuje se valuta i listingtype
        // provera da li korisnik vec ima margin acc sa tom valutom i tim listing type.
        // ako nema onda se kreira a bank account za koji je
        List<BankAccount> bankAccounts;
        if(marginAccountCreateDto.getCustomerId() != null) {
            bankAccounts = bankAccountService.getBankAccountsByCustomer(marginAccountCreateDto.getCustomerId());
        } else if(marginAccountCreateDto.getCompanyId() != null) {
            bankAccounts = bankAccountService.getBankAccountsByCompany(marginAccountCreateDto.getCompanyId());
        } else {
            Logger.error("Please provide customer id or company id in craete margin account request.");
            return false;
        }

        // get all bank accounts with the customer
        // bankAccounts su svi bank accounti koje mogu da iskoristim za pravljenje margin acc
        // sada sam ih profiltrirala tako da imam ovde samo bank accounte koji imaju odredjen currency
        bankAccounts = bankAccounts.stream()
                .filter(bankAccount ->
                        bankAccount.getCurrency().getCurrencyCode().equals(marginAccountCreateDto.getCurrency().getCurrencyCode()))
                .collect(Collectors.toList());

        // sad treba u for petlji da za svaki od tih bank accounta pitam da li vec postoji u mojim margin accountima
        List<String> finalBankAccountNumbers = bankAccounts.stream().map(BankAccount::getAccountNumber).toList();
        if(marginAccountRepository.findAll().stream().anyMatch(marginAccount ->
            marginAccount.getListingType().equals(marginAccountCreateDto.getListingType()) &&
            marginAccount.getCustomer().getCurrency().getCurrencyCode().equals(marginAccountCreateDto.getCurrency().getCurrencyCode()) &&
            finalBankAccountNumbers.contains(marginAccount.getCustomer().getAccountNumber())
        )) {
         // znaci da vec imamo account taj koji zelimo da napravimo
            Logger.error("Margin account already exists for ");
            return false;
        }

        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setMaintenanceMargin(marginAccountCreateDto.getMaintenanceMargin());
        marginAccount.setCurrency(marginAccountCreateDto.getCurrency());
        marginAccount.setBalance(marginAccount.getBalance());
        marginAccount.setListingType(marginAccountCreateDto.getListingType());
        marginAccountRepository.save(marginAccount);
        return true;
    }

    @Override
    public void depositToMarginAccount(MarginAccount marginAccount, Double fullAmount) {
        if(fullAmount < 0) {
            throw new InvalidCapitalAmountException(fullAmount);
        }
        double initialMargin = fullAmount * Constants.MARGIN_RATE;
        double loanedMoney = fullAmount - initialMargin;

        marginAccount.setBalance(marginAccount.getBalance() + initialMargin);
        marginAccount.setLoanValue(marginAccount.getLoanValue() + loanedMoney);
        marginAccountRepository.save(marginAccount);
    }

    @Override
    public void withdrawFromMarginAccount(MarginAccount marginAccount, Double amount) {
        if(amount < 0) {
            throw new InvalidCapitalAmountException(amount);
        }
        double newLoanValue = marginAccount.getLoanValue() - amount * Constants.MARGIN_RATE;

        marginAccount.setBalance(marginAccount.getBalance() - amount);
        marginAccount.setLoanValue(newLoanValue);
        marginAccountRepository.save(marginAccount);
    }

    @Override
    public List<MarginAccount> getAllMarginAccountEntities() {
        return this.marginAccountRepository.findAll();
    }

    @Override
    public void updateOnMarginSummary(MarginAccount marginAccount, Double equity, Double maintenanceMargin) {
        marginAccount.setMaintenanceMargin(maintenanceMargin);
        marginAccount.setBalance(equity);
        this.marginAccountRepository.save(marginAccount);
    }

    @Override
    public void triggerMarginCall(MarginAccount marginAccount) {
        if(marginAccount.getMarginCallLevel() != 0) {
            return;
        }
        marginAccount.setMarginCallLevel(1);
        this.marginAccountRepository.save(marginAccount);

        //Send email
        if(marginAccount.getCustomer().getCompany() == null) {
            emailService.sendEmail(marginAccount.getCustomer().getCustomer().getEmail(), "MARGIN CALL TRIGGERED", "Margin call triggered. Please deposit money to the margin account or liquidate some positions.");
        }

        //Schedule job to alert at midnight
        taskScheduler.schedule(new MarginCallMidnightJob(marginAccount, this), new MarginCallMidnightTrigger());
    }

    @Override
    public void triggerMarginCallAutomaticLiquidation(MarginAccount marginAccount) {
        if(marginAccount.getMarginCallLevel() != 1) {
            return;
        }
        marginAccount.setMarginCallLevel(2);
        this.marginAccountRepository.save(marginAccount);
    }
}