package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.*;
import rs.edu.raf.banka1.dtos.market_service.OptionsDto;
import rs.edu.raf.banka1.exceptions.*;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Getter
@Setter
@Service
public class CapitalServiceImpl implements CapitalService {

    private MarketService marketService;
    private BankAccountRepository bankAccountRepository;
    private CapitalRepository capitalRepository;
    private CapitalMapper capitalMapper;
    private BankAccountService bankAccountService;
    private final CompanyRepository companyRepository;

    public CapitalServiceImpl(BankAccountRepository bankAccountRepository,
                              CapitalRepository capitalRepository,
                              CapitalMapper capitalMapper,
                              BankAccountService bankAccountService,
                              MarketService marketService,
                              CompanyRepository companyRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.capitalRepository = capitalRepository;
        this.capitalMapper = capitalMapper;
        this.marketService = marketService;
        this.bankAccountService = bankAccountService;
        this.companyRepository = companyRepository;
    }

    @Override
    public Capital createCapital(ListingType listingType, Long listingId, Double total, Double reserved, BankAccount bankAccount) {
        Capital capital = new Capital();
        capital.setListingType(listingType);
        capital.setListingId(listingId);
        capital.setTotal(total);
        capital.setReserved(reserved);
        capital.setBankAccount(bankAccount);
        capital.setAverageBuyingPrice(0.0);

        if(capital.getListingType().equals(ListingType.STOCK)) {
            capital.setTicker(this.marketService.getStockById(capital.getListingId()).getTicker());
        } else if(capital.getListingType().equals(ListingType.FUTURE)) {
            capital.setTicker(this.marketService.getFutureById(capital.getListingId()).getTicker());
        } else if(capital.getListingType().equals(ListingType.FOREX)) {
            capital.setTicker(this.marketService.getForexById(capital.getListingId()).getTicker());
        } else if(capital.getListingType().equals(ListingType.OPTIONS)){
            capital.setTicker(this.marketService.getOptionsById(capital.getListingId()).getTicker());
        }

        this.capitalRepository.save(capital);

        return capital;
    }

    @Override
    public Capital getCapitalByListingIdAndTypeAndBankAccount(Long listingId, ListingType type, BankAccount bankAccount) {
        return capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount).orElseGet(() -> createCapital(type, listingId, 0D, 0D, bankAccount));
    }


    @Override
    public List<Capital> getCapitalStockForBank(BankAccount bankAccount) {
        return capitalRepository.getCapitalsByListingTypeAndBankAccount(ListingType.STOCK, bankAccount);
    }

    @Override
    public void reserveBalance(Long listingId, ListingType type, BankAccount bankAccount, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processReservation(capital, amount);
    }

    @Override
    public void commitReserved(Long listingId, ListingType type, BankAccount bankAccount, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processReservationCommited(capital, amount);
    }

    @Override
    public void releaseReserved(Long listingId, ListingType type, BankAccount bankAccount, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processReservationReleased(capital, amount);
    }

    @Override
    public void addBalance(Long listingId, ListingType type, BankAccount bankAccount, Double amount) {
//        Capital capital = capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        Capital capital = capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount).orElse(null);
        if (capital == null) {
            capital = createCapital(type, listingId, 0D, 0D, bankAccount);
        }
        processAddBalance(capital, amount);
    }

    @Override
    /*
    Samo za fizicka lica!
     */
    public List<PublicCapitalDto> getAllPublicStockCapitals() {
        List<Capital> publicCapitals = this.capitalRepository.findByBankAccount_CompanyNullAndListingTypeAndPublicTotalGreaterThan(ListingType.STOCK, 0d);
        List<PublicCapitalDto> publicCapitalDtos = new ArrayList<>();

        publicCapitals.forEach((Capital capital) -> {
            publicCapitalDtos.add(capitalMapper.capitalToPublicCapitalDto(capital));
        });

        return publicCapitalDtos;
    }

    @Override
    /*
    Samo za pravna lica!
     */
    public List<PublicCapitalDto> getAllPublicListingCapitals() {
        List<Capital> capitals = this.capitalRepository.getAllPublicCapitals();
        List<PublicCapitalDto> publicCapitalDtos = new ArrayList<>();

        capitals.forEach((Capital capital) -> {
            publicCapitalDtos.add(capitalMapper.capitalToPublicCapitalDto(capital));
        });

        return publicCapitalDtos;
    }

    @Override
    public Boolean addToPublicCapital(User userPrincipal, AddPublicCapitalDto setPublicCapitalDto) {
        if(userPrincipal.getCompany() != null) {
            //Add to company
            BankAccount companyBankAccount = bankAccountService.getBankAccountByCompanyAndCurrencyCode(userPrincipal.getCompany().getId(), Constants.DEFAULT_CURRENCY);
            addPublic(companyBankAccount, setPublicCapitalDto.getListingType(), setPublicCapitalDto.getListingId(), setPublicCapitalDto.getAddToPublic());
        } else {
            BankAccount individualBankAccount = bankAccountService.getBankAccountByCustomerAndCurrencyCode(userPrincipal.getUserId(), Constants.DEFAULT_CURRENCY);
            addPublicToIndividual(individualBankAccount, setPublicCapitalDto.getListingType(), setPublicCapitalDto.getListingId(), setPublicCapitalDto.getAddToPublic());
        }
        return true;
    }

    @Override
    public void removeFromPublicCapital(Long listingId, ListingType listingType, BankAccount bankAccount, Double amount) {
        Capital capital = getCapitalByListingIdAndTypeAndBankAccount(listingId, listingType, bankAccount);

        if(capital.getTotal() - capital.getReserved() < amount) throw new NotEnoughCapitalAvailableException();

        if(amount > capital.getPublicTotal()) throw new InvalidCapitalAmountException(amount);

        capital.setPublicTotal(capital.getPublicTotal() - amount);
        capitalRepository.save(capital);
    }

    @Override
    public CapitalDto getCapitalForStockId(Long stockId) {
        return null;
    }

    @Override
    public CapitalDto getCapitalForForexId(Long forexId) {
        return null;
    }



    @Override
    public List<AllPublicCapitalsDto> getAllPublicCapitals(Customer customer) {
        List<Capital> capitals = this.capitalRepository.getAllByPublicTotalGreaterThan(0d);

        List<AllPublicCapitalsDto> allPublicCapitalsDtos = new ArrayList<>();

        List<BankAccount> accounts;
        if(customer!=null) {
            accounts = customer.getAccountIds();
        }
        else{
            accounts = new ArrayList<>();
        }
        capitals.forEach((Capital capital) -> {
            if(!accounts.contains(capital.getBankAccount())){
                String name = "";
                if(capital.getBankAccount().getCompany() != null) {
                    name = capital.getBankAccount().getCompany().getCompanyName();
                } else {
                    name = capital.getBankAccount().getCustomer().getFirstName() + " " + capital.getBankAccount().getCustomer().getLastName();
                }

                allPublicCapitalsDtos.add(capitalMapper.capitalToAllPublicCapitalsDto(capital, name));
            }
        });


        return allPublicCapitalsDtos;
    }

    @Override
    public void updateAverageBuyingPrice(Long listingId, ListingType listingType, BankAccount bankAccount, Double newAverageBuyingPrice) {
        Capital capital = getCapitalByListingIdAndTypeAndBankAccount(listingId, listingType, bankAccount);
        capital.setAverageBuyingPrice(newAverageBuyingPrice);
        capitalRepository.save(capital);
    }


    @Override
    public void removeBalance(Long listingId, ListingType type, BankAccount bankAccount, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processRemoveBalance(capital, amount);
    }

//    @Override
//    public Double getCapital(String accountNumber) { // currencyID ?
//        BankAccount bankAccount = this.bankAccountRepository.findBankAccountByAccountNumber(accountNumber).orElseThrow(BankAccountNotFoundException::new);
//        Capital capital = this.capitalRepository.getCapitalByBankAccount(bankAccount).orElseThrow(() -> new CapitalNotFoundByBankAccountException(accountNumber));
//        return capital.getTotal()-capital.getReserved();
//    }

    private void processReservation(Capital capital, Double amount) {
        if(amount <= 0)
            throw new InvalidReservationAmountException();

        double available = capital.getTotal() - capital.getReserved();

        if(amount > available)
            throw new NotEnoughCapitalAvailableException();

        capital.setReserved(capital.getReserved() + amount);

        capitalRepository.save(capital);
    }
    private void processReservationCommited(Capital capital, Double amount) {
        if(amount <= 0)
            throw new InvalidReservationAmountException();

        if(amount > capital.getReserved()) {
            double leftAmount = amount - capital.getReserved();
            double available = capital.getTotal() - capital.getReserved();
            if(leftAmount > available) throw new InvalidReservationAmountException();
        }

        capital.setTotal(capital.getTotal() - amount);
        if(capital.getReserved() >= amount) {
            capital.setReserved(capital.getReserved() - amount);
        } else { // Amount higher than reserved
            capital.setReserved(0d);
        }

        capitalRepository.save(capital);
    }
    private void processReservationReleased(Capital capital, Double amount) {
        if(amount <= 0 || capital.getReserved() < amount)
            throw new InvalidReservationAmountException();

        capital.setReserved(capital.getReserved() - amount);
        capital.setTotal(capital.getTotal() + capital.getReserved());

        capitalRepository.save(capital);
    }
    private void processAddBalance(Capital capital, Double amount) {
        if(amount <= 0)
            throw new InvalidCapitalAmountException(amount);

        capital.setTotal(capital.getTotal() + amount);

        capitalRepository.save(capital);
    }
    private void processRemoveBalance(Capital capital, Double amount) {
        if(amount <= 0)
            throw new InvalidCapitalAmountException(amount);

        double available = capital.getTotal() - capital.getReserved();

        if(amount > available)
            throw new NotEnoughCapitalAvailableException();

        capital.setTotal(capital.getTotal() - amount);

        capitalRepository.save(capital);
    }
    @Override
    public Double estimateBalanceForex(Long forexId) {
        BankAccount bankAccount = bankAccountService.getDefaultBankAccount();
        ListingForexDto listingForexDto = this.marketService.getForexById(forexId);
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(forexId, ListingType.FOREX, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(forexId, ListingType.FOREX));
        return (capital.getTotal()-capital.getReserved())*listingForexDto.getPrice();
    }

    @Override
    public Double estimateBalanceFuture(Long futureId) {
        BankAccount bankAccount = bankAccountService.getDefaultBankAccount();
        ListingFutureDto listingFutureDto = this.marketService.getFutureById(futureId);
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(futureId, ListingType.FUTURE, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(futureId, ListingType.FUTURE));
        return (capital.getTotal()-capital.getReserved())*listingFutureDto.getPrice();
    }

    @Override
    public Double estimateBalanceStock(Long stockId) {
        BankAccount bankAccount = bankAccountService.getDefaultBankAccount();

        ListingStockDto listingStockDto = this.marketService.getStockById(stockId);
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(stockId, ListingType.STOCK, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(stockId, ListingType.STOCK));
        return (capital.getTotal()-capital.getReserved())*listingStockDto.getPrice();
    }

    @Override
    public Double estimateBalanceOptions(Long optionsId) {
        BankAccount bankAccount = bankAccountService.getDefaultBankAccount();
        OptionsDto optionsDto = this.marketService.getOptionsById(optionsId);
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(optionsId, ListingType.OPTIONS, bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(optionsId, ListingType.OPTIONS));
        return (capital.getTotal()-capital.getReserved())*optionsDto.getPrice();
    }

    @Override
    public List<CapitalProfitDto> getListingCapitalsQuantity(User user) {
        BankAccount bankAccount = null;

        if(user.getCompany() == null) {
            bankAccount = bankAccountService.getBankAccountByCustomerAndCurrencyCode(user.getUserId(), Constants.DEFAULT_CURRENCY);
        } else {
            bankAccount = bankAccountService.getBankAccountByCompanyAndCurrencyCode(user.getCompany().getId(), Constants.DEFAULT_CURRENCY);
        }

        return this.capitalRepository.findByBankAccount_AccountNumber(bankAccount.getAccountNumber()).stream()
                .filter(capital -> capital.getListingType() != null)
                .map(capital -> {
                    Double price = 0.0;
                    if(capital.getListingType().equals(ListingType.STOCK)) {
                        price = this.marketService.getStockById(capital.getListingId()).getPrice();
                    } else if(capital.getListingType().equals(ListingType.FUTURE)) {
                        price = this.marketService.getFutureById(capital.getListingId()).getPrice();
                    } else if(capital.getListingType().equals(ListingType.FOREX)) {
                        price = this.marketService.getForexById(capital.getListingId()).getPrice();
                    }
                    return capitalMapper.capitalToCapitalProfitDto(capital, price);
                })
                .toList();
    }

    @Override
    public boolean hasEnoughCapitalForOrder(MarketOrder order) {
        if(order.getOrderType().equals(OrderType.BUY)) {
            return checkCapitalForBuyOrder(order);
        }
        return checkCapitalForSellOrder(order);
    }

    private boolean checkCapitalForBuyOrder(MarketOrder order) {
//        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingType(order.getListingId(), order.getListingType()).orElseThrow(()-> new CapitalNotFoundByListingIdAndTypeException(order.getListingId(), order.getListingType()));
        BankAccount defaultAccount = bankAccountService.getDefaultBankAccount();
        if (order.getCustomer() != null) {
            defaultAccount = bankAccountService.findBankAccountByAccountNumber(order.getBankAccountNumber());
        }
        double available = defaultAccount.getAvailableBalance();

        return order.getPrice() <= available;
    }

    private boolean checkCapitalForSellOrder(MarketOrder order) {
        BankAccount bankAccount = bankAccountService.getDefaultBankAccount();
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(order.getListingId(), order.getListingType(), bankAccount).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(order.getListingId(), order.getListingType()));

        double available = capital.getTotal() - capital.getReserved();

        return order.getContractSize() <= available;
    }

    private void addPublicToIndividual(BankAccount bankAccount, ListingType listingType, Long listingId, Double amount) {
        if(!listingType.equals(ListingType.STOCK)) throw new OTCListingTypeException();
        addPublic(bankAccount, listingType, listingId, amount);
    }
    private void addPublic(BankAccount bankAccount, ListingType listingType, Long listingId, Double amount) {
        if(!bankAccount.getCurrency().getCurrencyCode().equals(Constants.DEFAULT_CURRENCY)) throw new OTCInvalidBankAccountCurrencyException();
        if(amount <= 0) throw new InvalidCapitalAmountException(amount);

        Capital capital = getCapitalByListingIdAndTypeAndBankAccount(listingId, listingType, bankAccount);

        double available = capital.getTotal() - capital.getReserved();

        if(capital.getPublicTotal() + amount > available) throw new NotEnoughCapitalAvailableException();

        capital.setPublicTotal(capital.getPublicTotal() + amount);
        capitalRepository.save(capital);
    }
}

