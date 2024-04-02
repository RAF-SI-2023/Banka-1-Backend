package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;

public interface RecipientsService {
    void createRecipient(Long customerId, CreatePaymentRecipientRequest request);
}
