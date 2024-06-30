package rs.edu.raf.banka1.services.implementations;


import org.springframework.cache.annotation.Cacheable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;
import rs.edu.raf.banka1.dtos.ExchangeRateDto;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.dtos.TransfersReportDto;
import rs.edu.raf.banka1.mapper.TransferMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;

    private final BankAccountRepository bankAccountRepository;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;
    private final TransferMapper transferMapper;
    private HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${exchangeRateAPIToken}")
    private String exchangeRateAPIToken;

    @Value("${exchangeRateApiUrl}")
    private String exchangeRateApiUrl;

    private final List<String> supportedCurrencies = List.of("USD", "AUD", "EUR", "CHF", "GBP", "JPY", "CAD");

    public TransferServiceImpl(TransferRepository transferRepository, BankAccountRepository bankAccountRepository, CurrencyRepository currencyRepository, TransferMapper transferMapper) {
        this.transferMapper = transferMapper;
        objectMapper = new ObjectMapper();
        this.currencyRepository = currencyRepository;
        this.transferRepository = transferRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public void seedExchangeRates(){
        if (initRsdCurrency()) return;
        seedExchangeRatesFromRsd(supportedCurrencies);
        seedExchangeRatesToRsd(supportedCurrencies);
    }

    private boolean initRsdCurrency() {
        Currency rsdCurrency = currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null);
        if (rsdCurrency == null) {
            Logger.error("Failed to load exchange rate for RSD!");
            return true;
        }
        rsdCurrency.setToRSD(1.0);
        rsdCurrency.setFromRSD(1.0);
        currencyRepository.save(rsdCurrency);
        return false;

    }

    private void seedExchangeRatesToRsd(List<String> supportedCurrencies) {
        try {
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
                        Logger.error("Failed to load exchange rate for currency: {}", currencyCode);
                        return;
                    }
                    currency.setToRSD(exchangeRate);
                    currencyRepository.save(currency);
                }else{
                    Logger.error("Error response code {}", response.statusCode());;
                }
            }
        } catch (IOException | InterruptedException e) {
            Logger.error(e, "An error occurred during exchange rate seeding");
            throw new RuntimeException(e);
        }
    }

    private void seedExchangeRatesFromRsd(List<String> supportedCurrencies) {
        HttpResponse<String> response;
        try {
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
                        Logger.error("Failed to load exchange rate for currency: {}",currencyCode);
                        return;
                    }
                    currency.setFromRSD(exchangeRate);
                    currencyRepository.save(currency);
                }

            }else{
                Logger.error("Error response code {}", response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            Logger.error(e, "An error occurred during exchange rate seeding");
            e.printStackTrace();
        }
    }

    @Override
    public Long createTransfer(CreateTransferRequest request) {
        Optional<BankAccount> senderAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber());
        Optional<BankAccount> recipientAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(request.getRecipientAccountNumber());

        if (senderAccountOpt.isEmpty() || recipientAccountOpt.isEmpty()) {
            Logger.error("Sender or recipient account not found for transfer creation");
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
    public String processTransfer(Long id) {
        Optional<Transfer> transferOpt = transferRepository.findById(id);
        if (transferOpt.isEmpty()) {
            Logger.error("Transfer not found for processing: {}", id);
            return "Transfer not found for processing";
        }
        Transfer transfer = transferOpt.get();
        BankAccount senderAccount = transfer.getSenderBankAccount();
        BankAccount recipientAccount = transfer.getRecipientBankAccount();
        Currency senderCurrency = senderAccount.getCurrency();
        Currency recipientCurrency = recipientAccount.getCurrency();
        double commission = Transfer.calculateCommission(transfer.getAmount());

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
            Logger.info("Transfer {} denied. Reason: invalid parameters or insufficient balance.", id);
            return "Transfer denied. Invalid parameters or insufficient balance";
        }

        BankAccount rsdBank = bankAccountRepository.findBankByCurrencyCode("RSD").orElse(null);
        BankAccount toBank = null;
        BankAccount fromBank = null;

        if (senderAccount.getAccountType() == AccountType.CURRENT) {
            toBank = rsdBank;
        }
        if (senderAccount.getAccountType() == AccountType.FOREIGN_CURRENCY && senderCurrency.getCurrencyCode() != null) {
            toBank = bankAccountRepository.findBankByCurrencyCode(senderCurrency.getCurrencyCode()).orElse(null);
        }
        if (recipientAccount.getAccountType() == AccountType.CURRENT) {
            fromBank = rsdBank;
        }
        if (recipientAccount.getAccountType() == AccountType.FOREIGN_CURRENCY && recipientCurrency.getCurrencyCode() != null) {
            fromBank = bankAccountRepository.findBankByCurrencyCode(recipientCurrency.getCurrencyCode()).orElse(null);
        }

        if (fromBank == null || toBank == null) {
            Logger.info("Transfer {} denied. Reason: bank not found for currency conversion.", id);
            transfer.setStatus(TransactionStatus.DENIED);
            transferRepository.save(transfer);
            return "Transfer denied. Reason: bank not found for currency conversion.";
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

        Logger.info("Transfer {} processed successfully.", id);
        transfer.setStatus(TransactionStatus.COMPLETE);
        transfer.setConvertedAmount(convertedAmount);
        transfer.setCommission(commission);
        transfer.setCurrencyFrom(senderCurrency);
        transfer.setCurrencyTo(recipientCurrency);
        transfer.setExchangeRate(exchangeRate);

        transferRepository.save(transfer);
        bankAccountRepository.saveAll(List.of(fromBank, toBank, senderAccount, recipientAccount));
        return null;
    }

    @Override
    @Cacheable(value="getTransferById", key="#id")
    public TransferDto getTransferById(Long id) {
        Optional<Transfer> paymentOpt = transferRepository.findById(id);
        return paymentOpt.map(transferMapper::transferToTransferDto).orElse(null);
    }

    @Override
    @Cacheable(value="getAllTransfersForAccountNumber", key= "#accountNumber")
    public List<TransferDto> getAllTransfersForAccountNumber(String accountNumber) {
        Optional<BankAccount> bankAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(accountNumber);
        return bankAccountOpt.map(bankAccount ->
                bankAccount.getTransfers().stream()
                        .map(transferMapper::transferToTransferDto)
                        .collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    @Override
    public List<ExchangeRateDto> getExchangeRates() {
        List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();
        List<String> currencies = new ArrayList<>(supportedCurrencies);
        currencies.add("RSD");
        for (String baseCurrencySymbol : currencies) {
            for (String quoteCurrencySymbol : currencies) {
                //we don't want exchange rate from EUR to EUR
                if (baseCurrencySymbol.equals(quoteCurrencySymbol)) {
                    continue;
                }
                Currency baseCurrency = currencyRepository.findCurrencyByCurrencyCode(baseCurrencySymbol).orElse(null);
                Currency quoteCurrency = currencyRepository.findCurrencyByCurrencyCode(quoteCurrencySymbol).orElse(null);
                if (baseCurrency == null || quoteCurrency == null) {
                    Logger.error("Currency not found for exchange rate: {} to {}", baseCurrencySymbol, quoteCurrencySymbol);
                    continue;
                }
                double exchangeRate = baseCurrency.getToRSD() * quoteCurrency.getFromRSD();
                exchangeRate /= 1 + Transfer.commissionPercentage();
                exchangeRateDtos.add(new ExchangeRateDto(baseCurrencySymbol, quoteCurrencySymbol, exchangeRate));
            }
        }
        return exchangeRateDtos;
    }

    @Override
    public TransfersReportDto getTransfersReport() {
        List<Transfer> allTransfers = transferRepository.findAll();
        double totalProfit = 0;

        TransfersReportDto transfersReportDto = new TransfersReportDto();

        transfersReportDto.setTransfers(allTransfers.stream()
                        .map(transferMapper::transferToTransferDto)
                        .collect(Collectors.toList()));

        for (Transfer t:allTransfers){
//            double commisison = Transfer.calculateCommission(t.getAmount());
            totalProfit+=t.getCommission();
        }
        transfersReportDto.setProfit(totalProfit);

        return transfersReportDto;
    }

    //for testing only
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    //for testing
    public void setExchangeRateAPIToken(String exchangeRateAPIToken) {
        this.exchangeRateAPIToken = exchangeRateAPIToken;
    }

    //for testing
    public void setExchangeRateApiUrl(String exchangeRateApiUrl) {
        this.exchangeRateApiUrl = exchangeRateApiUrl;
    }
}
