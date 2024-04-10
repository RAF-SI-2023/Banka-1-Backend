package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.PaymentRecipientDto;
import rs.edu.raf.banka1.mapper.RecipientMapper;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.PaymentRecipient;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.repositories.PaymentRecipientRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;

import rs.edu.raf.banka1.services.implementations.RecipientsServiceImpl;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecipientsServiceImplTest {

    @Mock
    private PaymentRecipientRepository paymentRecipientRepository;
    @Mock
    private CustomerRepository customerRepository;

    private RecipientMapper recipientMapper;

    private RecipientsServiceImpl recipientsService;

    @BeforeEach
    void setUp(){
        recipientMapper = new RecipientMapper(customerRepository);
        recipientsService = new RecipientsServiceImpl(paymentRecipientRepository,customerRepository,recipientMapper);
    }

//    @Test
//    public void testCreateRecipient() {
//        CreatePaymentRecipientRequest createPaymentRecipientRequest = new CreatePaymentRecipientRequest();
//        createPaymentRecipientRequest.setFirstName("Keri");
//        createPaymentRecipientRequest.setLastName("Show");
//        createPaymentRecipientRequest.setBankAccountNumber("12345");
//
//        Long customerId = 1L;
//        Customer customer = new Customer();
//        customer.setUserId(customerId);
//
//        PaymentRecipient recipient = new PaymentRecipient();
//        recipient.setCustomer(customer);
//        recipient.setRecipientAccountNumber(createPaymentRecipientRequest.getBankAccountNumber());
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
//        when(recipientMapper.createRecipientRequestToRecipientForCustomer(customerId, createPaymentRecipientRequest)).thenReturn(recipient);
//
//        recipientsService.createRecipient(customerId, createPaymentRecipientRequest);
//
//        verify(paymentRecipientRepository).save(recipient);
//    }

    @Test
    public void testEditRecipientWhenRecipientExists() {
        PaymentRecipientDto request = new PaymentRecipientDto();
        request.setId(1L);
        request.setFirstName("John Doe");
        request.setBankAccountNumber("1234567890");

        PaymentRecipient existingRecipient = new PaymentRecipient();
        existingRecipient.setId(1L);
        existingRecipient.setFirstName("Jane Smith");
        existingRecipient.setRecipientAccountNumber("0000000");

        when(paymentRecipientRepository.findById(request.getId())).thenReturn(Optional.of(existingRecipient));

        boolean result = recipientsService.editRecipient(request);

        assertTrue(result);
        verify(paymentRecipientRepository).save(any(PaymentRecipient.class));
    }

    @Test
    public void editRecipientTestRecipientInvalid() {
        PaymentRecipientDto request = new PaymentRecipientDto();
        request.setId(100L);

        when(paymentRecipientRepository.findById(request.getId())).thenReturn(Optional.empty());

        boolean result = recipientsService.editRecipient(request);

        assertFalse(result);
        verify(paymentRecipientRepository, never()).save(any(PaymentRecipient.class));
    }

    @Test
    public void getAllRecipientsForCustomerTestCustomerValid() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setUserId(customerId);

        List<PaymentRecipient> recipients = new ArrayList<>();
        customer.setRecipients(recipients);

        List<PaymentRecipientDto> expectedDtos = recipients.stream()
                .map(recipientMapper::recipientToRecipientDto)
                .collect(Collectors.toList());

        when(customerRepository.findByUserId(customerId)).thenReturn(Optional.of(customer));


        List<PaymentRecipientDto> resultDtos = recipientsService.getAllRecipientsForCustomer(customerId);

        assertNotNull(resultDtos);
        assertEquals(expectedDtos.size(), resultDtos.size());
    }

    @Test
    public void getAllRecipientsForCustomerTestCustomerInvalid() {
        Long nonExistentCustomerId = 100L;
        when(customerRepository.findByUserId(nonExistentCustomerId)).thenReturn(Optional.empty());

        List<PaymentRecipientDto> resultDtos = recipientsService.getAllRecipientsForCustomer(nonExistentCustomerId);

        assertTrue(resultDtos.isEmpty());
    }

}