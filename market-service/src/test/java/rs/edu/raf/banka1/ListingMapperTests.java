package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rs.edu.raf.banka1.mapper.ListingMapper;
import rs.edu.raf.banka1.model.Listing;
import rs.edu.raf.banka1.model.ListingHistory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ListingMapperTests {

    @Autowired
    private ListingMapper listingMapper;

    @BeforeEach
    public void setUp() {
        listingMapper = new ListingMapper();
    }

    @Test
    public void listingModelUpdateTest() {
        // Mock necessary objects
        Listing listing = mock(Listing.class);

        // Call the method
        double price = 75.0;
        double high = 100.0;
        double low = 50.0;
        double change = 25.0;
        int volume = 1000;

        listingMapper.listingModelUpdate(listing, price, high, low, change, volume);

        // Verify method calls on listing and check if the fields are set correctly
        verify(listing).setPrice(price);
        verify(listing).setAsk(high); // Assuming setAsk corresponds to setting the high value
        verify(listing).setBid(low); // Assuming setBid corresponds to setting the low value
        verify(listing).setChanged(change);
        verify(listing).setVolume(volume);

        // Verify that lastRefresh is set to current timestamp
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        verify(listing).setLastRefresh(currentTime);

    }

    @Test
    public void createListingHistoryModelTest(){
        // Create a new instance of ListingMapper
        ListingMapper listingMapper = new ListingMapper();

        // Call the method
        String ticker = "XYZ";
        long date = 1647433200; // Example date, you can adjust it as needed
        double price = 75.0;
        double ask = 100.0;
        double bid = 50.0;
        double changed = 25.0;
        int volume = 1000;

        ListingHistory result = listingMapper.createListingHistoryModel(ticker, date, price, ask, bid, changed, volume);

        // Assert the values set in the created ListingHistoryModel
        assertEquals(ticker, result.getTicker());
        assertEquals(date, result.getDate());
        assertEquals(price, result.getPrice());
        assertEquals(ask, result.getAsk());
        assertEquals(bid, result.getBid());
        assertEquals(changed, result.getChanged());
        assertEquals(volume, result.getVolume());
    }

    @Test
    public void updateHistoryListingWithNewDataTest(){
        // Mock necessary objects
        ListingHistory oldModel = mock(ListingHistory.class);
        ListingHistory newModel = mock(ListingHistory.class);
        ListingMapper listingMapper = new ListingMapper();

        // Set up stubbed behavior for newModel
        double newPrice = 80.0;
        double newAsk = 105.0;
        double newBid = 55.0;
        double newChanged = 30.0;
        int newVolume = 1200;

        when(newModel.getPrice()).thenReturn(newPrice);
        when(newModel.getAsk()).thenReturn(newAsk);
        when(newModel.getBid()).thenReturn(newBid);
        when(newModel.getChanged()).thenReturn(newChanged);
        when(newModel.getVolume()).thenReturn(newVolume);

        // Call the method
        ListingHistory result = listingMapper.updateHistoryListingWithNewData(oldModel, newModel);

        // Verify method calls on oldModel and check if the fields are set correctly
        verify(oldModel).setPrice(newPrice);
        verify(oldModel).setAsk(newAsk);
        verify(oldModel).setBid(newBid);
        verify(oldModel).setChanged(newChanged);
        verify(oldModel).setVolume(newVolume);

        // Verify that the returned model is the same as the old model
        assertEquals(oldModel, result);
    }

    @Test
    public void listingModelToListongHistoryModelTest() {
        // Mock necessary objects
        Listing listing = mock(Listing.class);

        // Set up stubbed behavior for listing
        String ticker = "XYZ";
        double price = 75.0;
        double ask = 100.0;
        double bid = 50.0;
        double changed = 25.0;
        int volume = 1000;
        long lastRefresh = 1647433200; // Example Unix timestamp, adjust as needed

        when(listing.getTicker()).thenReturn(ticker);
        when(listing.getLastRefresh()).thenReturn(lastRefresh);
        when(listing.getPrice()).thenReturn(price);
        when(listing.getAsk()).thenReturn(ask);
        when(listing.getBid()).thenReturn(bid);
        when(listing.getChanged()).thenReturn(changed);
        when(listing.getVolume()).thenReturn(volume);

        // Call the method
        ListingHistory result = listingMapper.listingModelToListongHistoryModel(listing);

        // Verify the fields set in the created ListingHistoryModel
        assertEquals(ticker, result.getTicker());

        // Convert the Unix timestamp to LocalDate
        LocalDate localDate = Instant.ofEpochSecond(lastRefresh).atZone(ZoneOffset.UTC).toLocalDate();

        // Get the Unix timestamp for the beginning of the day
        long beginningOfDayUnixTimestamp = localDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        assertEquals(beginningOfDayUnixTimestamp, result.getDate());
        assertEquals(price, result.getPrice());
        assertEquals(ask, result.getAsk());
        assertEquals(bid, result.getBid());
        assertEquals(changed, result.getChanged());
        assertEquals(volume, result.getVolume());
    }
}
