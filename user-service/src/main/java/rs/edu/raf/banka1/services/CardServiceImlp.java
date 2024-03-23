package rs.edu.raf.banka1.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.Card;
import rs.edu.raf.banka1.repositories.CardRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class CardServiceImlp implements CardService{
    @Autowired
    private CardRepository cardRepository;

    @Override
    public List<Card> getAllCardsByAccountNumber(String accountNumber) {
        return cardRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public Card createCard(String cardType, String cardName, String accountNumber, Integer limit) {
        Card card = new Card();

        card.setCardNumber(createUniqueCardNumber());
        card.setCardType(cardType);
        card.setCardName(cardName);

        long creationDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

//      expiration date is 5 years from now
        long expirationDate = LocalDate.now().plusYears(5).atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        card.setCreationDate(creationDate);
        card.setExpirationDate(expirationDate);
        card.setAccountNumber(accountNumber);
        card.setCvv(createCvv());
        card.setCardLimit(limit);
        card.setIsActivated(true);

        return card;
    }

    public String createUniqueCardNumber() {
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

    public String createCvv() {
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
