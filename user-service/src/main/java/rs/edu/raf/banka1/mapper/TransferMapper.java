package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Transfer;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.requests.CreateTransferRequest;

import java.util.Optional;

@Component
public class TransferMapper {
    public TransferDto transferToTransferDto(Transfer transfer) {
        TransferDto dto = new TransferDto();
        dto.setAmount(transfer.getAmount());
        dto.setId(transfer.getId());
        dto.setStatus(transfer.getStatus());
        dto.setCommission(transfer.getCommission());
        dto.setConvertedAmount(transfer.getConvertedAmount());
        dto.setDateOfPayment(transfer.getDateOfPayment());
        dto.setExchangeRate(transfer.getExchangeRate());
        if (transfer.getSenderBankAccount() != null) {
            dto.setSenderAccountNumber(transfer.getSenderBankAccount().getAccountNumber());
            dto.setSenderName(transfer.getSenderBankAccount().getCustomer().getFirstName() + " " + transfer.getSenderBankAccount().getCustomer().getLastName());
        }
        if (transfer.getRecipientBankAccount() != null) {
            dto.setRecipientAccountNumber(transfer.getRecipientBankAccount().getAccountNumber());
        }
        dto.setPreviousCurrency(transfer.getCurrencyFrom().getCurrencyCode());
        dto.setExchangedTo(transfer.getCurrencyTo().getCurrencyCode());
        dto.setProfit(transfer.getCommission());

        return dto;
    }

}
