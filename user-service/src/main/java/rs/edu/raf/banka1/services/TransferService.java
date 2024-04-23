package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.ExchangeRateDto;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.model.Transfer;
import rs.edu.raf.banka1.requests.CreateTransferRequest;

import java.util.List;

public interface TransferService {
//    List<ExchangeRateDto> getExchangeRates(String fromCurrency);
    //dto

    void seedExchangeRates();

    Long createTransfer(CreateTransferRequest createTransferRequest);

    String processTransfer(Long id);

    TransferDto getTransferById(Long id);

    List<TransferDto> getAllTransfersForAccountNumber(String accountNumber);

    List<ExchangeRateDto> getExchangeRates();
}
