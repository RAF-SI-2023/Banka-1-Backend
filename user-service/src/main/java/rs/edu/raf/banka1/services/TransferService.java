package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.Transfer;
import rs.edu.raf.banka1.requests.CreateTransferRequest;

import java.util.List;

public interface TransferService {
//    List<ExchangeRateDto> getExchangeRates(String fromCurrency);
    //dto

    void seedExchangeRates();

    Transfer createTransfer(CreateTransferRequest createTransferRequest);
}
