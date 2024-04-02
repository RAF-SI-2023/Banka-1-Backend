package rs.edu.raf.banka1.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.PaymentRecipient;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;
import rs.edu.raf.banka1.requests.EditPaymentRecipientRequest;

@Component
public class RecipientMapper {

    private final CustomerRepository customerRepository;

    @Autowired
    public RecipientMapper(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public PaymentRecipient createRecipientRequestToRecipientForCustomer(Long customerId, CreatePaymentRecipientRequest request) {
        PaymentRecipient recipient = new PaymentRecipient();
        recipient.setCustomer(customerRepository.getReferenceById(customerId));
        recipient.setRecipientAccountNumber(request.getBankAccountNumber());
        recipient.setFirstName(request.getFirstName());
        recipient.setLastName(request.getLastName());

        return recipient;
    }

    public PaymentRecipient editRecipientRequestToRecipient(PaymentRecipient paymentRecipient, EditPaymentRecipientRequest request) {
        if (request.getBankAccountNumber() != null) {
            paymentRecipient.setRecipientAccountNumber(request.getBankAccountNumber());
        }
        if (request.getFirstName() != null) {
            paymentRecipient.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            paymentRecipient.setLastName(request.getLastName());
        }

        return paymentRecipient;
    }
}
