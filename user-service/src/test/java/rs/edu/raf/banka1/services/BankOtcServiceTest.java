package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.dtos.market_service.OptionsDto;
import rs.edu.raf.banka1.dtos.otc_trade.EditMyStockDto;
import rs.edu.raf.banka1.dtos.otc_trade.FrontendOfferDto;
import rs.edu.raf.banka1.dtos.otc_trade.MyStockDto;
import rs.edu.raf.banka1.dtos.otc_trade.OfferDto;
import rs.edu.raf.banka1.model.listing.BankOTCStock;
import rs.edu.raf.banka1.model.listing.MyStock;
import rs.edu.raf.banka1.model.offer.MyOffer;
import rs.edu.raf.banka1.model.offer.Offer;
import rs.edu.raf.banka1.model.offer.OfferStatus;
import rs.edu.raf.banka1.repositories.otc_trade.BankOTCStockRepository;
import rs.edu.raf.banka1.repositories.otc_trade.MyOfferRepository;
import rs.edu.raf.banka1.repositories.otc_trade.MyStockRepository;
import rs.edu.raf.banka1.repositories.otc_trade.OfferRepository;
import rs.edu.raf.banka1.services.implementations.otc_trade.BankOtcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class BankOtcServiceTest {
    @Mock
    private OfferRepository offerRepository;

    @Mock
    private MyStockRepository myStockRepository;

    @Mock
    private BankOTCStockRepository bankOTCStockRepository;

    @Mock
    private MyOfferRepository myOfferRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BankOtcService bankOtcService;

    private static final String URL_TO_BANK3 =  "https://banka-3-dev.si.raf.edu.rs/exchange-service/api/v1/otcTrade";

    @BeforeEach
    public void setUp(){
        bankOtcService.setRestTemplate(restTemplate);
    }

    @Test
    public void testFindAllStocks() {
        // Arrange
        MyStock stock1 = new MyStock();
        stock1.setTicker("AAPL");
        stock1.setAmount(100);

        MyStock stock2 = new MyStock();
        stock2.setTicker("GOOGL");
        stock2.setAmount(200);

        List<MyStock> stocks = Arrays.asList(stock1, stock2);
        when(myStockRepository.findAllByCompanyIdAndPublicAmountGreaterThan(1L, 0)).thenReturn(stocks);

        // Act
        List<MyStockDto> result = bankOtcService.findAllStocks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MyStockDto dto1 = result.get(0);
        System.out.println(dto1);
        assertEquals("AAPL", dto1.getTicker());
        assertEquals((Integer) 100, dto1.getAmount());

        MyStockDto dto2 = result.get(1);
        assertEquals("GOOGL", dto2.getTicker());
        assertEquals((Integer) 200, dto2.getAmount());

        verify(myStockRepository, times(1)).findAllByCompanyIdAndPublicAmountGreaterThan(1L, 0);
    }

    @Test
    public void testReceiveOffer_Processing() {
        // Arrange
        OfferDto offerDto = new OfferDto();
        offerDto.setTicker("AAPL");
        offerDto.setAmount(50);
        offerDto.setPrice(150.0);
        offerDto.setIdBank(123L);

        MyStock myStock = new MyStock();
        myStock.setTicker("AAPL");
        myStock.setPublicAmount(100);

        when(myStockRepository.findByTickerAndCompanyId("AAPL", 1L)).thenReturn(myStock);

        // Act
        Offer result = bankOtcService.receiveOffer(offerDto);

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals((Integer) 50, result.getAmount());
        assertEquals((Double) 150.0, result.getPrice());
        assertEquals((Long) 123L, result.getIdBank());
        assertEquals(OfferStatus.PROCESSING, result.getOfferStatus());

        verify(myStockRepository, times(1)).findByTickerAndCompanyId("AAPL", 1L);
        verify(offerRepository, times(1)).save(result);
    }

    @Test
    public void testReceiveOffer_Declined() {
        // Arrange
        OfferDto offerDto = new OfferDto();
        offerDto.setTicker("AAPL");
        offerDto.setAmount(150); // Amount greater than available stock
        offerDto.setPrice(150.0);
        offerDto.setIdBank(123L);

        MyStock myStock = new MyStock();
        myStock.setTicker("AAPL");
        myStock.setPublicAmount(100);

        when(myStockRepository.findByTickerAndCompanyId("AAPL", 1L)).thenReturn(myStock);

        // Act
        Offer result = bankOtcService.receiveOffer(offerDto);

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals((Integer) 150, result.getAmount());
        assertEquals((Double) 150.0, result.getPrice());
        assertEquals((Long) 123L, result.getIdBank());
        assertEquals(OfferStatus.DECLINED, result.getOfferStatus());

        verify(myStockRepository, times(1)).findByTickerAndCompanyId("AAPL", 1L);
        verify(offerRepository, times(1)).save(result);
    }

    @Test
    public void testReceiveOffer_StockNotFound() {
        // Arrange
        OfferDto offerDto = new OfferDto();
        offerDto.setTicker("AAPL");
        offerDto.setAmount(50);
        offerDto.setPrice(150.0);
        offerDto.setIdBank(123L);

        when(myStockRepository.findByTickerAndCompanyId("AAPL", 1L)).thenReturn(null);

        // Act
        Offer result = bankOtcService.receiveOffer(offerDto);

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals((Integer) 50, result.getAmount());
        assertEquals((Double) 150.0, result.getPrice());
        assertEquals((Long) 123L, result.getIdBank());
        assertEquals(OfferStatus.DECLINED, result.getOfferStatus());

        verify(myStockRepository, times(1)).findByTickerAndCompanyId("AAPL", 1L);
        verify(offerRepository, times(1)).save(result);
    }

    @Test
    public void testOfferDeclined_OfferFound() {
        // Arrange
        Long offerId = 1L;
        MyOffer myOffer = new MyOffer();
        myOffer.setMyOfferId(offerId);
        myOffer.setOfferStatus(OfferStatus.PROCESSING);

        when(myOfferRepository.findById(offerId)).thenReturn(Optional.of(myOffer));

        // Act
        boolean result = bankOtcService.offerDeclined(offerId);

        // Assert
        assertTrue(result);
        assertEquals(OfferStatus.DECLINED, myOffer.getOfferStatus());

        verify(myOfferRepository, times(1)).findById(offerId);
        verify(myOfferRepository, times(1)).save(myOffer);
    }

    @Test
    public void testOfferDeclined_OfferNotFound() {
        // Arrange
        Long offerId = 1L;

        when(myOfferRepository.findById(offerId)).thenReturn(Optional.empty());

        // Act
        boolean result = bankOtcService.offerDeclined(offerId);

        // Assert
        assertFalse(result);

        verify(myOfferRepository, times(1)).findById(offerId);
        verify(myOfferRepository, never()).save(any());
    }

    @Test
    public void testDeleteOffer_OfferFound() {
        // Arrange
        Long offerId = 1L;
        Offer offer = new Offer();
        offer.setOfferId(offerId);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        // Act
        boolean result = bankOtcService.deleteOffer(offerId);

        // Assert
        assertTrue(result);
        verify(offerRepository, times(1)).findById(offerId);
        verify(offerRepository, times(1)).delete(offer);
    }

    @Test
    public void testDeleteOffer_OfferNotFound() {
        // Arrange
        Long offerId = 1L;

        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // Act
        boolean result = bankOtcService.deleteOffer(offerId);

        // Assert
        assertFalse(result);
        verify(offerRepository, times(1)).findById(offerId);
        verify(offerRepository, never()).delete(any());
    }

    @Test
    public void acceptOfferWeDontHaveStock(){
        MyOffer myOffer = new MyOffer();
        myOffer.setTicker("AAPL");
        myOffer.setAmount(50);
        myOffer.setPrice(150.0);
        myOffer.setMyOfferId(1L);

        when(myOfferRepository.findById(any())).thenReturn(Optional.of(myOffer));

        when(myStockRepository.findByTicker(any())).thenReturn(null);

        when(bankOTCStockRepository.findByTicker(any())).thenReturn(null);

        boolean out = bankOtcService.offerAccepted(1L);

        assertTrue(out);

        verify(myOfferRepository).save(any());
    }

    @Test
    public void acceptOfferWeHaveStock(){
        MyOffer myOffer = new MyOffer();
        myOffer.setTicker("AAPL");
        myOffer.setAmount(50);
        myOffer.setPrice(150.0);
        myOffer.setMyOfferId(1L);

        when(myOfferRepository.findById(any())).thenReturn(Optional.of(myOffer));

        when(myStockRepository.findByTicker(any())).thenReturn(new MyStock());

        when(bankOTCStockRepository.findByTicker(any())).thenReturn(null);

        MyStock myStock = new MyStock();
        myStock.setAmount(100);
        myStock.setPublicAmount(100);
        when(myStockRepository.findByTickerAndCompanyId(any(), any())).thenReturn(myStock);

        boolean out = bankOtcService.offerAccepted(1L);

        assertTrue(out);

        verify(myOfferRepository).save(any());
    }

    @Test
    public void editStock(){
        MyStock myStock = new MyStock();
        myStock.setAmount(100);
        myStock.setPublicAmount(100);
        myStock.setTicker("AAPL");

        when(myStockRepository.findByTickerAndCompanyId(any(), any())).thenReturn(myStock);

        EditMyStockDto editMyStockDto = new EditMyStockDto();
        editMyStockDto.setPublicAmount(50);
        editMyStockDto.setPrice(100.0);

        boolean out = bankOtcService.editMyStock(editMyStockDto);

        assertTrue(out);

        verify(myStockRepository).save(any());
    }

    @Test
    public void getStocksFromBank3(){
        List<MyStockDto> answer = new ArrayList<>();
        MyStockDto myStockDto = new MyStockDto();
        myStockDto.setAmount(100);
        myStockDto.setTicker("AAPL");
        answer.add(myStockDto);
        when(restTemplate.exchange(
                eq(URL_TO_BANK3 +"/getOurStocks"),
                eq(HttpMethod.GET),
                eq(null),
                eq(new ParameterizedTypeReference<List<MyStockDto>>(){})
        )).thenReturn(new ResponseEntity<>(answer, HttpStatus.OK));

        bankOtcService.getBankStocks();
    }

    @Test
    public void makeOffer(){
        FrontendOfferDto frontendOfferDto = new FrontendOfferDto();

        frontendOfferDto.setAmount(100);
        frontendOfferDto.setPrice(100.0);
        frontendOfferDto.setTicker("AAPL");

        when(restTemplate.exchange(
                eq(URL_TO_BANK3 +"/sendOffer/bank1"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Boolean>(){})
        )).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        MyOffer offer = new MyOffer();
        offer.setTicker("AAPL");
        offer.setAmount(100);
        offer.setPrice(100.0);
        offer.setMyOfferId(1L);
        when(myOfferRepository.save(any())).thenReturn(offer);

        bankOtcService.makeOffer(frontendOfferDto);
    }

    @Test
    public void acceptOffer(){
        Offer offer = new Offer();

        when(offerRepository.findById(any())).thenReturn(Optional.of(offer));

        offer.setTicker("AAPL");
        offer.setAmount(100);
        offer.setPrice(100.0);

        MyStock myStock = new MyStock();
        myStock.setAmount(100);
        myStock.setPublicAmount(100);

        when(myStockRepository.findByTickerAndCompanyId(any(), any())).thenReturn(myStock);


        boolean out = bankOtcService.acceptOffer(1L);

        assertTrue(out);

        verify(offerRepository, times(2)).save(any());
    }

    @Test
    public void declineOffer(){
        Offer offer = new Offer();

        when(offerRepository.findById(any())).thenReturn(Optional.of(offer));

        boolean out = bankOtcService.declineOffer(1L);

        assertTrue(out);

        verify(offerRepository).save(any());
    }

    @Test
    public void deleteMyOffer(){
        MyOffer myOffer = new MyOffer();
        myOffer.setMyOfferId(1L);

        when(myOfferRepository.findById(any())).thenReturn(Optional.of(myOffer));

        boolean out = bankOtcService.deleteMyOffer(1L);

        assertTrue(out);

        verify(myOfferRepository).delete(any());
    }

}
