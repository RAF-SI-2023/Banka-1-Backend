package rs.edu.raf.banka1.mappers;

import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.CardDto;
import rs.edu.raf.banka1.model.Card;
import rs.edu.raf.banka1.mapper.CardMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardMapperTest {

    private final CardMapper cardMapper = new CardMapper();

    @Test
    void testToDto() {
        // Arrange
        Card card = new Card();
        card.setId(1L);
        card.setCardNumber("1234567890123456");
        card.setCardType("VISA");
        card.setCardName("John Doe");
        card.setCreationDate(100000L);
        card.setExpirationDate(10000000L);
        card.setAccountNumber("9876543210");
        card.setCvv("123");
        card.setCardLimit(5000);
        card.setIsActivated(true);

        // Act
        CardDto cardDto = cardMapper.toDto(card);

        // Assert
        assertEquals(card.getId(), cardDto.getId());
        assertEquals(card.getCardNumber(), cardDto.getCardNumber());
        assertEquals(card.getCardType(), cardDto.getCardType());
        assertEquals(card.getCardName(), cardDto.getCardName());
        assertEquals(card.getCreationDate(), cardDto.getCreationDate());
        assertEquals(card.getExpirationDate(), cardDto.getExpirationDate());
        assertEquals(card.getAccountNumber(), cardDto.getAccountNumber());
        assertEquals(card.getCvv(), cardDto.getCvv());
        assertEquals(card.getCardLimit(), cardDto.getCardLimit());
        assertEquals(card.getIsActivated(), cardDto.getIsActivated());
    }
}
