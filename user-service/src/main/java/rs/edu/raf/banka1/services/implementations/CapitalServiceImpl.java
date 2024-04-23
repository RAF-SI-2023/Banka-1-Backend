package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.CapitalProfitDto;
import rs.edu.raf.banka1.exceptions.*;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.List;


@Getter
@Setter
@Service
public class CapitalServiceImpl implements CapitalService {

    private MarketService marketService;
    private BankAccountRepository bankAccountRepository;
    private CapitalRepository capitalRepository;
    private CapitalMapper capitalMapper;
    public CapitalServiceImpl(BankAccountRepository bankAccountRepository,
                              CapitalRepository capitalRepository,
                              CapitalMapper capitalMapper,
                              MarketService marketService) {
        this.bankAccountRepository = bankAccountRepository;
        this.capitalRepository = capitalRepository;
        this.capitalMapper = capitalMapper;
        this.marketService = marketService;
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

        if(capital.getListingType().equals(ListingType.STOCK)) {
            capital.setTicker(this.marketService.getStockById(capital.getListingId()).getTicker());
        } else if(capital.getListingType().equals(ListingType.FUTURE)) {
            capital.setTicker(this.marketService.getFutureById(capital.getListingId()).getTicker());
        } else if(capital.getListingType().equals(ListingType.FOREX)) {
            capital.setTicker(this.marketService.getForexById(capital.getListingId()).getTicker());
        }

        return capital;
    }

    @Override
    public Capital getCapitalByCurrencyCode(String currencyCode) {
        return capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode).orElseThrow(() -> new CapitalNotFoundByCodeException(currencyCode));
    }

    @Override
    public Capital getCapitalByListingIdAndType(Long listingId, ListingType type) {
        return capitalRepository.getCapitalByListingIdAndListingType(listingId, type).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
    }

    @Override
    public void reserveBalance(String currencyCode, Double amount) {
        Capital capital = capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode).orElseThrow(() -> new CapitalNotFoundByCodeException(currencyCode));
        processReservation(capital, amount);
    }

    @Override
    public void commitReserved(String currencyCode, Double amount) {
        Capital capital = capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode).orElseThrow(() -> new CapitalNotFoundByCodeException(currencyCode));
        processReservationCommited(capital, amount);
    }

    @Override
    public void releaseReserved(String currencyCode, Double amount) {
        Capital capital = capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode).orElseThrow(() -> new CapitalNotFoundByCodeException(currencyCode));
        processReservationReleased(capital, amount);
    }

    @Override
    public void addBalance(String currencyCode, Double amount) {
        Capital capital = capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode).orElseThrow(() -> new CapitalNotFoundByCodeException(currencyCode));
        processAddBalance(capital, amount);
    }

    @Override
    public void removeBalance(String currencyCode, Double amount) {
        Capital capital = capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode).orElseThrow(() -> new CapitalNotFoundByCodeException(currencyCode));
        processRemoveBalance(capital, amount);
    }

    @Override
    public void reserveBalance(Long listingId, ListingType type, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingType(listingId, type).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processReservation(capital, amount);
    }

    @Override
    public void commitReserved(Long listingId, ListingType type, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingType(listingId, type).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processReservationCommited(capital, amount);
    }

    @Override
    public void releaseReserved(Long listingId, ListingType type, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingType(listingId, type).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processReservationReleased(capital, amount);
    }

    @Override
    public void addBalance(Long listingId, ListingType type, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingType(listingId, type).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processAddBalance(capital, amount);
    }

    @Override
    public void removeBalance(Long listingId, ListingType type, Double amount) {
        Capital capital = capitalRepository.getCapitalByListingIdAndListingType(listingId, type).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(listingId, type));
        processRemoveBalance(capital, amount);
    }

    @Override
    public Double getCapital(String accountNumber) { // currencyID ?
        BankAccount bankAccount = this.bankAccountRepository.findBankAccountByAccountNumber(accountNumber).orElseThrow(BankAccountNotFoundException::new);
        Capital capital = this.capitalRepository.getCapitalByBankAccount(bankAccount).orElseThrow(() -> new CapitalNotFoundByBankAccountException(accountNumber));
        return capital.getTotal()-capital.getReserved();
    }

    private void processReservation(Capital capital, Double amount) {
        if(amount <= 0)
            throw new InvalidReservationAmountException();

        BankAccount bankAccount = capital.getBankAccount();

        double available = capital.getTotal() - capital.getReserved();

        if(amount > available)
            throw new NotEnoughCapitalAvailableException();

        capital.setReserved(capital.getReserved() + amount);

        capitalRepository.save(capital);

        if(bankAccount != null) {
            if(bankAccount.getAvailableBalance() < amount) {
                throw new NotEnoughCapitalAvailableException();
            }
            bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() - amount);
            bankAccountRepository.save(bankAccount);
        }
    }
    private void processReservationCommited(Capital capital, Double amount) {
        BankAccount bankAccount = capital.getBankAccount();

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

        if(bankAccount != null) {
            bankAccount.setBalance(bankAccount.getBalance() - amount);
            bankAccountRepository.save(bankAccount);
        }

        capitalRepository.save(capital);
    }
    private void processReservationReleased(Capital capital, Double amount) {
        BankAccount bankAccount = capital.getBankAccount();

        if(amount <= 0 || capital.getReserved() < amount)
            throw new InvalidReservationAmountException();

        capital.setReserved(capital.getReserved() - amount);
        capital.setTotal(capital.getTotal() + capital.getReserved());

        if(bankAccount != null) {
            bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() + amount);
            bankAccountRepository.save(bankAccount);
        }

        capitalRepository.save(capital);
    }
    private void processAddBalance(Capital capital, Double amount) {
        if(amount <= 0)
            throw new InvalidCapitalAmountException(amount);

        BankAccount bankAccount = capital.getBankAccount();


        capital.setTotal(capital.getTotal() + amount);

        if(bankAccount != null) {
            bankAccount.setBalance(bankAccount.getBalance() + amount);
            bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() + amount);
            bankAccountRepository.save(bankAccount);
        }

        capitalRepository.save(capital);
    }
    private void processRemoveBalance(Capital capital, Double amount) {
        BankAccount bankAccount = capital.getBankAccount();

        if(amount <= 0)
            throw new InvalidCapitalAmountException(amount);

        double available = capital.getTotal() - capital.getReserved();

        if(amount > available)
            throw new NotEnoughCapitalAvailableException();

        capital.setTotal(capital.getTotal() - amount);

        if(bankAccount != null) {
            if(bankAccount.getAvailableBalance() < amount) {
                throw new NotEnoughCapitalAvailableException();
            }
            bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() - amount);
            bankAccount.setBalance(bankAccount.getBalance() - amount);
            bankAccountRepository.save(bankAccount);
        }

        capitalRepository.save(capital);
    }
    @Override
    public Double estimateBalanceForex(Long forexId) {
        ListingForexDto listingForexDto = this.marketService.getForexById(forexId);
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingType(forexId, ListingType.FOREX).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(forexId, ListingType.FOREX));
        return (capital.getTotal()-capital.getReserved())*listingForexDto.getPrice();
    }

    @Override
    public Double estimateBalanceFuture(Long futureId) {
        ListingFutureDto listingFutureDto = this.marketService.getFutureById(futureId);
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingType(futureId, ListingType.FUTURE).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(futureId, ListingType.FUTURE));
        return (capital.getTotal()-capital.getReserved())*listingFutureDto.getPrice();
    }

    @Override
    public Double estimateBalanceStock(Long stockId) {
        ListingStockDto listingStockDto = this.marketService.getStockById(stockId);
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingType(stockId, ListingType.STOCK).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(stockId, ListingType.STOCK));
        return (capital.getTotal()-capital.getReserved())*listingStockDto.getPrice();
    }

    @Override
    public List<CapitalProfitDto> getListingCapitalsQuantity() {
        return this.capitalRepository.findAll().stream()
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
        Capital capital = this.capitalRepository.getCapitalByCurrency_CurrencyCode(Constants.DEFAULT_CURRENCY).orElseThrow(()-> new CapitalNotFoundByCodeException(Constants.DEFAULT_CURRENCY));

        double available = capital.getTotal() - capital.getReserved();

        return order.getPrice() <= available;
    }

    private boolean checkCapitalForSellOrder(MarketOrder order) {
        Capital capital = this.capitalRepository.getCapitalByListingIdAndListingType(order.getListingId(), order.getListingType()).orElseThrow(() -> new CapitalNotFoundByListingIdAndTypeException(order.getListingId(), order.getListingType()));

        double available = capital.getTotal() - capital.getReserved();

        return order.getContractSize() <= available;
    }
}

