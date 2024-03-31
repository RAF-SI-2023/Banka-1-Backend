package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.Card;

import java.util.List;

public interface CardService {
    public List<Card> getAllCardsByAccountNumber(String accountNumber);
    public Card createCard(String cardType, String cardName, String accountNumber, Integer limit);
    public void saveCard(Card card);
    public List<Card> getAllCardsByCustomerId(Long customerId);

}
