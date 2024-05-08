package rs.edu.raf.banka1.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;
import rs.edu.raf.banka1.exceptions.ModifyRecipientException;
import rs.edu.raf.banka1.exceptions.RecipientNotFoundException;
import rs.edu.raf.banka1.mapper.RecipientMapper;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.PaymentRecipient;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.repositories.PaymentRecipientRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;
import rs.edu.raf.banka1.dtos.PaymentRecipientDto;
import rs.edu.raf.banka1.services.RecipientsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipientsServiceImpl implements RecipientsService {
    private final PaymentRecipientRepository paymentRecipientRepository;
    private final CustomerRepository customerRepository;
    private final RecipientMapper recipientMapper;

    @Autowired
    public RecipientsServiceImpl(PaymentRecipientRepository paymentRecipientRepository, CustomerRepository customerRepository, RecipientMapper recipientMapper) {
        this.paymentRecipientRepository = paymentRecipientRepository;
        this.customerRepository = customerRepository;
        this.recipientMapper = recipientMapper;
    }

    @Override
    public void createRecipient(Long customerId, CreatePaymentRecipientRequest request) {
        PaymentRecipient recipient = recipientMapper.createRecipientRequestToRecipientForCustomer(customerId, request);
        paymentRecipientRepository.save(recipient);
    }

    @Override
    public boolean editRecipient(PaymentRecipientDto request) {
        PaymentRecipient recipient = paymentRecipientRepository.findById(request.getId())
            .orElseThrow(()->new ModifyRecipientException(request.getId()));

        PaymentRecipient newRecipient = recipientMapper.PaymentRecipientDtoToRecipient(recipient, request);
        paymentRecipientRepository.save(newRecipient);
        Logger.info("Recipient edited successfully: {}", newRecipient.getId());
        return true;
    }

    @Override
    public List<PaymentRecipientDto> getAllRecipientsForCustomer(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findByUserId(customerId);
        return customerOpt.map(customer ->
                customer.getRecipients().stream()
                .map(recipientMapper::recipientToRecipientDto)
                .collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    @Override
    public boolean removeRecipient(Long id) {
        PaymentRecipient recipient = paymentRecipientRepository.findById(id)
            .orElseThrow(()->new RecipientNotFoundException(id));

        paymentRecipientRepository.delete(recipient);
        Logger.info("Recipient removed successfully: {}", id);
        return true;
    }
}
