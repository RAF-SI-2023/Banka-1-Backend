package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Card;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.CardRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {
    @Spy
    private CardServiceImlp cardService;

    @BeforeEach
    void setUp() {
        CardRepository cardRepository = mock(CardRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        cardService.setCardRepository(cardRepository);
        cardService.setCustomerRepository(customerRepository);
    }

    @Test
    public void createCardTest() {
        String cardType = "debit";
        String cardName = "debit card";
        String accountNumber = "123456789";
        Integer limit = 1000;
        int cvv_length = 3;
        int card_number_length = 16;

        Card card = cardService.createCard(cardType, cardName, accountNumber, limit);

//        Assertions
        assertEquals(card.getCardType(), cardType);
        assertEquals(card.getCardName(), cardName);
        assertEquals(card.getAccountNumber(), accountNumber);
        assertEquals(card.getCardLimit(), limit);
        assertEquals(card.getIsActivated(), true);
        assertEquals(card.getCardNumber().length(), card_number_length);
        assertEquals(card.getCvv().length(), cvv_length);
    }

    @Test
    public void getAllCardsByCustomerIdTestCustomerNotFound() {
        Long customerId = 100L;
        when(cardService.getCustomerRepository().findById(customerId)).thenReturn(Optional.empty());

        List<Card> cards = cardService.getAllCardsByCustomerId(customerId);

        assertEquals(cards.size(), 0);
    }

    @Test
    public void getAllCardsByCustomerIdTestCustomerWithTwoAccounts(){
        // Customer with 2 accounts, each account has 2 cards
        // This should return 4 cards
        Long customerId = 1L;
        Customer customer = new Customer();

        BankAccount account1 = new BankAccount();
        account1.setAccountNumber("1234567890");
        BankAccount account2 = new BankAccount();
        account2.setAccountNumber("0987654321");

        Card card1 = new Card();
        card1.setAccountNumber(account1.getAccountNumber());
        Card card2 = new Card();
        card2.setAccountNumber(account1.getAccountNumber());
        Card card3 = new Card();
        card3.setAccountNumber(account2.getAccountNumber());
        Card card4 = new Card();
        card4.setAccountNumber(account2.getAccountNumber());

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        customer.setAccountIds(accounts);

        List<Card> expectedCards = List.of(card1, card2, card3, card4);

        when(cardService.getCustomerRepository().findById(customerId)).thenReturn(Optional.of(customer));
        when(cardService.getCardRepository().findByAccountNumber(account1.getAccountNumber())).thenReturn(List.of(card1, card2));
        when(cardService.getCardRepository().findByAccountNumber(account2.getAccountNumber())).thenReturn(List.of(card3, card4));

        // When
        List<Card> actualCards = cardService.getAllCardsByCustomerId(customerId);

        // Then
        assertEquals(expectedCards.size(), actualCards.size());
        assertTrue(actualCards.containsAll(expectedCards));
    }
}
