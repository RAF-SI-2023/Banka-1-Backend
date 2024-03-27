package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.CardDto;
import rs.edu.raf.banka1.model.Card;

@Component
public class CardMapper {
    public CardDto toDto(Card card) {
        CardDto cardDto = new CardDto();

        cardDto.setId(card.getId());
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setCardType(card.getCardType());
        cardDto.setCardName(card.getCardName());
        cardDto.setCreationDate(card.getCreationDate());
        cardDto.setExpirationDate(card.getExpirationDate());
        cardDto.setAccountNumber(card.getAccountNumber());
        cardDto.setCvv(card.getCvv());
        cardDto.setCardLimit(card.getCardLimit());
        cardDto.setIsActivated(card.getIsActivated());

        return cardDto;
    }

}
