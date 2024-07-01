package rs.edu.raf.banka1.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferMapperTest {

    private TransferMapper transferMapper = new TransferMapper();

    @Test
    void transferToTransferDto() {
        Currency fromRSDToUSD = new Currency(
                1L,
                "United States Dollar",
                "USD",
                "$",
                "United States",
                "US Dollar",
                true,
                0.01, // Example conversion rate from RSD to USD
                null
        );

        // Instance for converting from another currency (e.g., USD) to RSD
        Currency fromUSDToRSD = new Currency(
                2L,
                "United States Dollar",
                "USD",
                "$",
                "United States",
                "US Dollar",
                true,
                null,
                100.0 // Example conversion rate from USD to RSD
        );

        BankAccount senderBankAccount = new BankAccount();
        senderBankAccount.setAccountNumber("sender");
        Customer sender = new Customer();
        sender.setFirstName("name");
        sender.setLastName("lastname");
        senderBankAccount.setCustomer(sender);

        BankAccount recipientBankAccount = new BankAccount();
        recipientBankAccount.setAccountNumber("recipient");

        Transfer transfer = new Transfer();

        transfer.setAmount(1.0);
        transfer.setId(1L);
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setCommission(2.0);
        transfer.setConvertedAmount(3.0);
        transfer.setExchangeRate(4.0);
        transfer.setSenderBankAccount(senderBankAccount);
        transfer.setRecipientBankAccount(recipientBankAccount);
        transfer.setCurrencyFrom(fromRSDToUSD);
        transfer.setCurrencyTo(fromUSDToRSD);

        TransferDto res = transferMapper.transferToTransferDto(transfer);

        assertEquals(res.getAmount(), 1.0);
        assertEquals(res.getId(), 1L);
        assertEquals(res.getStatus(), TransactionStatus.PROCESSING);
        assertEquals(res.getCommission(), 2.0);
        assertEquals(res.getConvertedAmount(), 3.0);
        assertEquals(res.getExchangeRate(), 4.0);
        assertEquals(res.getSenderAccountNumber(), senderBankAccount.getAccountNumber());
        assertEquals(res.getSenderName(), sender.getFirstName() + " " + sender.getLastName());
        assertEquals(res.getRecipientAccountNumber(), recipientBankAccount.getAccountNumber());
        assertEquals(transfer.getCurrencyFrom(), fromRSDToUSD);
        assertEquals(transfer.getCurrencyTo(), fromUSDToRSD);
    }
}