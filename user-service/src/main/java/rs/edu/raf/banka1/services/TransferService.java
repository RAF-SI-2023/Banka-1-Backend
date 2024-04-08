package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ExchangeRate;
import rs.edu.raf.banka1.model.Transfer;
import rs.edu.raf.banka1.requests.CreateTransferRequest;

import java.util.List;

public interface TransferService {
    List<ExchangeRate> getExchangeRates(String fromCurrency);
    //dto

    Transfer createTransfer(CreateTransferRequest createTransferRequest);
}
