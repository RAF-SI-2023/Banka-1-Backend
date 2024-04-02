package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;
import rs.edu.raf.banka1.dtos.PaymentRecipientDto;

import java.util.List;

public interface RecipientsService {
    void createRecipient(Long customerId, CreatePaymentRecipientRequest request);

    boolean editRecipient(PaymentRecipientDto request);

    List<PaymentRecipientDto> getAllRecipientsForCustomer(Long customerId);
}
