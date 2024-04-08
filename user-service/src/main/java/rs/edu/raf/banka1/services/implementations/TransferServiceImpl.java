package rs.edu.raf.banka1.services.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.TransferRepository;
import rs.edu.raf.banka1.requests.CreateTransferRequest;
import rs.edu.raf.banka1.services.TransferService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;

    private final BankAccountRepository bankAccountRepository;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;

    @Value("${exchangeRateAPIToken}")
    private String exchangeRateAPIToken;

    @Value("${exchangeRateApiUrl}")
    private String exchangeRateApiUrl;

    public TransferServiceImpl(TransferRepository transferRepository, BankAccountRepository bankAccountRepository, CurrencyRepository currencyRepository) {
        objectMapper = new ObjectMapper();
        this.currencyRepository = currencyRepository;
        this.transferRepository = transferRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public void seedExchangeRates(){
        List<String> supportedCurrencies = List.of("USD", "AUD", "EUR", "CHF", "GBP", "JPY", "CAD");
        if (initRsdCurrency()) return;
        seedExchangeRatesFromRsd(supportedCurrencies);
        seedExchangeRatesToRsd(supportedCurrencies);
    }

    private boolean initRsdCurrency() {
        Currency rsdCurrency = currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null);
        if (rsdCurrency == null) {
            System.out.println("Failed to load exchange rate for RSD!");
            return true;
        }
        rsdCurrency.setToRSD(1.0);
        rsdCurrency.setFromRSD(1.0);
        currencyRepository.save(rsdCurrency);
        return false;
    }

    private void seedExchangeRatesToRsd(List<String> supportedCurrencies) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            for (String currencyCode : supportedCurrencies) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(exchangeRateApiUrl + exchangeRateAPIToken + "/latest/" + currencyCode))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode conversionRateNode = rootNode.get("conversion_rates");

                    double exchangeRate = conversionRateNode.get("RSD").asDouble();

                    Currency currency = currencyRepository.findCurrencyByCurrencyCode(currencyCode).orElse(null);
                    if (currency == null) {
                        System.out.println("Failed to load exchange rate for currency: " + currencyCode);
                        return;
                    }
                    currency.setToRSD(exchangeRate);
                    currencyRepository.save(currency);
                }else{
                    System.out.println("Error response code" + response.statusCode());
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void seedExchangeRatesFromRsd(List<String> supportedCurrencies) {
        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(exchangeRateApiUrl + exchangeRateAPIToken + "/latest/RSD"))
                    .GET()
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode conversionRateNode = rootNode.get("conversion_rates");

                for (String currencyCode : supportedCurrencies) {
                    double exchangeRate = conversionRateNode.get(currencyCode).asDouble();
                    Currency currency = currencyRepository.findCurrencyByCurrencyCode(currencyCode).orElse(null);
                    if (currency == null) {
                        System.out.println("Failed to load exchange rate for currency: " + currencyCode);
                        return;
                    }
                    currency.setFromRSD(exchangeRate);
                    currencyRepository.save(currency);
                }

            }else{
                System.out.println("Error response code" + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public List<ExchangeRate> getExchangeRates(String fromCurrencyCode) {
//
//    }

    @Override
    public Long createTransfer(CreateTransferRequest request) {
        Optional<BankAccount> senderAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber());
        Optional<BankAccount> recipientAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(request.getRecipientAccountNumber());

        if (senderAccountOpt.isEmpty() || recipientAccountOpt.isEmpty()) {
            return -1L;
        }
        BankAccount senderAccount = senderAccountOpt.get();
        BankAccount recipientAccount = recipientAccountOpt.get();

        Transfer transfer = new Transfer();
        transfer.setSenderBankAccount(senderAccount);
        transfer.setRecipientBankAccount(recipientAccount);
        transfer.setAmount(request.getAmount());
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setDateOfPayment(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());

        // set on processing transfer
        transfer.setConvertedAmount(null);
        transfer.setCurrencyFrom(null);
        transfer.setCurrencyTo(null);
        transfer.setExchangeRate(null);
        transfer.setCommission(null);

        transferRepository.save(transfer);
       return transfer.getId();
    }

    @Override
    public void processTransfer(Long id) {
        Optional<Transfer> transferOpt = transferRepository.findById(id);
        if (transferOpt.isEmpty()) {
            return;
        }
        Transfer transfer = transferOpt.get();
        BankAccount senderAccount = transfer.getSenderBankAccount();
        BankAccount recipientAccount = transfer.getRecipientBankAccount();
        Currency senderCurrency = senderAccount.getCurrency();
        Currency recipientCurrency = recipientAccount.getCurrency();
        double commission = Payment.calculateCommission(transfer.getAmount());

        //todo isto customer

        if (
                transfer.getStatus() != TransactionStatus.PROCESSING
            || transfer.getAmount() + commission > senderAccount.getAvailableBalance()
            || transfer.getAmount() + commission > senderAccount.getBalance()
            || senderCurrency == null
            || senderCurrency.getFromRSD() == null
            || senderCurrency.getToRSD() == null
            || recipientCurrency == null
            || recipientCurrency.getFromRSD() == null
            || recipientCurrency.getToRSD() == null
            || recipientCurrency.getId().equals(senderCurrency.getId())
            || !Objects.equals(senderAccount.getCustomer().getUserId(), recipientAccount.getCustomer().getUserId())
        ) {
            transfer.setStatus(TransactionStatus.DENIED);
            transferRepository.save(transfer);
            return;
        }

        BankAccount rsdBank = bankAccountRepository.findBankByCurrencyCode("RSD").orElse(null);
        BankAccount toBank = null;
        BankAccount fromBank = null;

        if (senderAccount.getAccountType() == AccountType.CURRENT) {
            toBank = rsdBank;
        }
        if (senderAccount.getAccountType() == AccountType.FOREIGN_CURRENCY) {
            toBank = bankAccountRepository.findBankByCurrencyCode(senderCurrency.getCurrencyCode()).orElse(null);
        }
        if (recipientAccount.getAccountType() == AccountType.CURRENT) {
            fromBank = rsdBank;
        }
        if (recipientAccount.getAccountType() == AccountType.FOREIGN_CURRENCY) {
            fromBank = bankAccountRepository.findBankByCurrencyCode(recipientCurrency.getCurrencyCode()).orElse(null);
        }

        if (fromBank == null || toBank == null) {
            transfer.setStatus(TransactionStatus.DENIED);
            transferRepository.save(transfer);
            return;
        }

        double exchangeRate = senderCurrency.getToRSD() * recipientCurrency.getFromRSD();
        double convertedAmount = transfer.getAmount() * exchangeRate;

        //available balance
        senderAccount.setAvailableBalance(senderAccount.getAvailableBalance() - transfer.getAmount() - commission);
        toBank.setAvailableBalance(toBank.getAvailableBalance() + transfer.getAmount() + commission);

        fromBank.setAvailableBalance(fromBank.getAvailableBalance() - convertedAmount);
        recipientAccount.setAvailableBalance(recipientAccount.getAvailableBalance() + convertedAmount);

        // balance
        senderAccount.setBalance(senderAccount.getBalance() - transfer.getAmount() - commission);
        toBank.setBalance(toBank.getBalance() + transfer.getAmount() + commission);

        fromBank.setBalance(fromBank.getBalance() - convertedAmount);
        recipientAccount.setBalance(recipientAccount.getBalance() + convertedAmount);

        transfer.setStatus(TransactionStatus.COMPLETE);
        transfer.setConvertedAmount(convertedAmount);
        transfer.setCommission(commission);
        transfer.setCurrencyFrom(senderCurrency);
        transfer.setCurrencyTo(recipientCurrency);
        transfer.setExchangeRate(exchangeRate);

        transferRepository.save(transfer);
        bankAccountRepository.saveAll(List.of(fromBank, toBank, senderAccount, recipientAccount));
    }

}
