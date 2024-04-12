package rs.edu.raf.banka1.mappers;

import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.mapper.TransferMapper;
import rs.edu.raf.banka1.model.TransactionStatus;
import rs.edu.raf.banka1.model.Transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferMapperTest {

    @Test
    void testTransferToTransferDto() {
        // Arrang
        TransferMapper mapper = new TransferMapper();

        Transfer transfer = new Transfer();
        transfer.setAmount(100.0);
        transfer.setStatus(TransactionStatus.COMPLETE);
        transfer.setCommission(5.0);
        transfer.setConvertedAmount(95.0);
        transfer.setDateOfPayment(100L);
        transfer.setExchangeRate(1.0);

        // Act
        TransferDto transferDto = mapper.transferToTransferDto(transfer);

        // Assert
        assertEquals(transfer.getAmount(), transferDto.getAmount());
        assertEquals(transfer.getStatus(), transferDto.getStatus());
        assertEquals(transfer.getCommission(), transferDto.getCommission());
        assertEquals(transfer.getConvertedAmount(), transferDto.getConvertedAmount());
        assertEquals(transfer.getDateOfPayment(), transferDto.getDateOfPayment());
        assertEquals(transfer.getExchangeRate(), transferDto.getExchangeRate());
    }
}
