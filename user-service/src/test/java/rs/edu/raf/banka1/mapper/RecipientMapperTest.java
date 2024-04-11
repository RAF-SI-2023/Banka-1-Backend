package rs.edu.raf.banka1.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.PaymentRecipientDto;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.PaymentRecipient;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecipientMapperTest {
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private RecipientMapper recipientMapper;

    @Test
    void createRecipientRequestToRecipientForCustomer() {
        CreatePaymentRecipientRequest request = new CreatePaymentRecipientRequest();
        request.setFirstName("name");
        request.setLastName("lastname");
        request.setBankAccountNumber("1234");

        Customer customer = new Customer();
        customer.setUserId(1L);
        customer.setEmail("test");

        when(customerRepository.getReferenceById(1L)).thenReturn(customer);

        PaymentRecipient res = recipientMapper.createRecipientRequestToRecipientForCustomer(1L, request);

        assertEquals(res.getCustomer(), customer);
        assertEquals(res.getFirstName(), request.getFirstName());
        assertEquals(res.getLastName(), request.getLastName());
        assertEquals(res.getRecipientAccountNumber(), request.getBankAccountNumber());
    }

    @Test
    void paymentRecipientDtoToRecipient() {
        PaymentRecipient recipient = new PaymentRecipient();
        recipient.setRecipientAccountNumber("1234");

        PaymentRecipientDto dto = new PaymentRecipientDto();
        dto.setBankAccountNumber("4321");

        PaymentRecipient res = recipientMapper.PaymentRecipientDtoToRecipient(recipient, dto);

        assertEquals(res.getRecipientAccountNumber(), "4321");
    }

    @Test
    void recipientToRecipientDto() {
        PaymentRecipient recipient = new PaymentRecipient();
        recipient.setRecipientAccountNumber("1234");
        recipient.setFirstName("name");
        recipient.setLastName("lastName");
        recipient.setId(1L);

        PaymentRecipientDto dto = recipientMapper.recipientToRecipientDto(recipient);

        assertEquals("1234", dto.getBankAccountNumber());
        assertEquals("name", dto.getFirstName());
        assertEquals("lastName", dto.getLastName());
        assertEquals(1L, dto.getId());
    }
}