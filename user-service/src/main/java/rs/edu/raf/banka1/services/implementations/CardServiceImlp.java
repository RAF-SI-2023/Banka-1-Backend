package rs.edu.raf.banka1.services.implementations;

import org.springframework.cache.annotation.Cacheable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Card;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.CardRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.services.CardService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Service
public class CardServiceImlp implements CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CustomerRepository customerRepository;


    @Override
    @Cacheable(value = "getAllCardsByAccountNumber", key = "#accountNumber")
    public List<Card> getAllCardsByAccountNumber(String accountNumber) {
        return cardRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public Card createCard(String cardType, String cardName, BankAccount account, Integer limit) {
        Card card = new Card();

        card.setCardNumber(createUniqueCardNumber());
        card.setCardType(cardType);
        card.setCardName(cardName);

        long creationDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

//      expiration date is 5 years from now
        long expirationDate = LocalDate.now().plusYears(5).atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        card.setCreationDate(creationDate);
        card.setExpirationDate(expirationDate);
        card.setAccountNumber(account.getAccountNumber());
        card.setBankAccount(account);
        card.setCvv(createCvv());
        card.setCardLimit(limit);
        card.setIsActivated(true);

        return card;
    }

    @Override
    @Cacheable(value = "getAllCardsByCustomerId", key = "#customerId")
    public List<Card> getAllCardsByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        List<Card> cards = new ArrayList<>();

        if (customer == null) {
            return cards;
        }

        for(BankAccount bankAccount : customer.getAccountIds()){
            cards.addAll(cardRepository.findByAccountNumber(bankAccount.getAccountNumber()));
        }
        return cards;
    }

    private String createUniqueCardNumber() {
        // generate unique card number of 16 digits
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append((int) (Math.random() * 10));
        }

//        check if card number already exists in database
        if (cardRepository.findByCardNumber(cardNumber.toString()).isEmpty()) {
            return cardNumber.toString();
        }

        return createUniqueCardNumber();
    }

    private String createCvv() {
        // generate cvv of 3 digits
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvv.append((int) (Math.random() * 10));
        }

        return cvv.toString();
    }

    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }
}
