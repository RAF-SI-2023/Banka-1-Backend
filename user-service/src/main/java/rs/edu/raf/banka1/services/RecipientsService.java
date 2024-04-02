package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;
import rs.edu.raf.banka1.requests.EditPaymentRecipientRequest;

public interface RecipientsService {
    void createRecipient(Long customerId, CreatePaymentRecipientRequest request);

    boolean editRecipient(EditPaymentRecipientRequest request);
}
