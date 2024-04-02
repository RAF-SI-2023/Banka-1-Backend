package rs.edu.raf.banka1.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.RecipientMapper;
import rs.edu.raf.banka1.model.PaymentRecipient;
import rs.edu.raf.banka1.repositories.PaymentRecipientRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;
import rs.edu.raf.banka1.services.RecipientsService;

@Service
public class RecipientsServiceImpl implements RecipientsService {
    private final PaymentRecipientRepository paymentRecipientRepository;
    private final RecipientMapper recipientMapper;

    @Autowired
    public RecipientsServiceImpl(PaymentRecipientRepository paymentRecipientRepository, RecipientMapper recipientMapper) {
        this.paymentRecipientRepository = paymentRecipientRepository;
        this.recipientMapper = recipientMapper;
    }

    @Override
    public void createRecipient(Long customerId, CreatePaymentRecipientRequest request) {
        PaymentRecipient recipient = recipientMapper.createRecipientRequestToRecipientForCustomer(customerId, request);
        paymentRecipientRepository.save(recipient);
    }
}
