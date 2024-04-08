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


import java.util.List;
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
        seedExchangeRatesFromRsd(supportedCurrencies);
        seedExchangeRatesToRsd(supportedCurrencies);
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
    public Transfer createTransfer(CreateTransferRequest request) {
        Optional<BankAccount> senderAccountOpt = bankAccountRepository.findBankAccountByAccountNumber(request.getSenderAccountNumber());
        if (senderAccountOpt.isEmpty()) return null;
        BankAccount senderAccount = senderAccountOpt.get();

        Transfer transfer = new Transfer();
        transfer.setSenderBankAccount(senderAccount);
//        transfer.setRecipientAccountNumber(request.getRecipientAccountNumber());
        transfer.setAmount(request.getAmount());

        transfer.setStatus(TransactionStatus.PROCESSING);
//        payment.setCommissionFee(Payment.calculateCommission(request.getAmount()));
//        payment.setDateOfPayment(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());
       return null;
    }

}
