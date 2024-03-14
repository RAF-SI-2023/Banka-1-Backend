package rs.edu.raf.banka1.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ExchangeModel;
import rs.edu.raf.banka1.repositories.ExchangeRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeService {
    private final String EXCHANGE_CSV_PATH = "csv/mic.csv";

    private Map<String, String> marketOpenTimes = new HashMap<>();
    private Map<String, String> marketCloseTimes = new HashMap<>();

    private ExchangeRepository exchangeRepository;

    private List<ExchangeModel> exchanges = new ArrayList<>();

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
        initTimes();
        parseExchangeCSV();
    }

    private void parseExchangeCSV() {
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(EXCHANGE_CSV_PATH));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                ExchangeModel exchange = new ExchangeModel();
                exchange.setMicCode(nextRecord[0]);  // MIC
                exchange.setExchangeName(nextRecord[3]); // Market Name
                exchange.setExchangeAcronym(nextRecord[7]); // Acronym
                exchange.setCountry(nextRecord[8]); // ISO Country Code
                exchange.setCurrency(nextRecord[6]); // Market Category Code
                exchange.setTimeZone(nextRecord[15]); // Time Zone

                exchange.setOpenTime(marketOpenTimes.get(exchange.getCountry()));
                exchange.setCloseTime(marketCloseTimes.get(exchange.getCountry()));
                exchanges.add(exchange);
            }
            if (!exchanges.isEmpty()) {
                exchangeRepository.saveAll(exchanges);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private void setTime(String country, String openTime, String closeTime) {
        marketOpenTimes.put(country, openTime);
        marketCloseTimes.put(country, closeTime);
    }

    private void initTimes() {
        setTime("US", "09:30", "16:00");
        setTime("CA", "09:30", "16:00");
        setTime("GB", "08:00", "16:00");
        setTime("CZ", "09:00", "16:20");
        setTime("DE", "08:00", "20:00");
        setTime("DE", "09:00", "17:30");
        setTime("DE", "08:00", "22:00");
        setTime("DK", "09:00", "17:00");
        setTime("FI", "10:00", "18:30");
        setTime("FR", "09:00", "17:30");
        setTime("IE", "08:00", "16:30");
        setTime("IT", "09:00", "17:25");
        setTime("LU", "09:00", "17:35");
        setTime("NO", "09:00", "16:30");
        setTime("SE", "09:00", "17:30");
        setTime("ES", "09:00", "17:30");
        setTime("CH", "09:00", "17:30");
        setTime("UA", "10:00", "17:30");
        setTime("AT", "08:55", "17:35");
        setTime("BG", "10:10", "16:55");
        setTime("GR", "10:15", "17:20");
        setTime("PL", "09:00", "17:35");
        setTime("RO", "10:00", "17:45");
        setTime("ME", "09:30", "14:00");
        setTime("HU", "09:00", "17:00");
        setTime("HR", "09:30", "15:55");

        setTime("AU", "10:00", "16:00");
        setTime("CH", "09:30", "15:00");
        setTime("HK", "09:20", "16:00");
        setTime("IN", "09:15", "15:30");
        setTime("JP", "09:00", "15:00");
        setTime("KR", "09:00", "15:00");
        setTime("MY", "09:00", "17:00");
        setTime("NZ", "10:00", "17:00");
        setTime("PH", "09:30", "15:30");
        setTime("SG", "09:00", "17:00");
        setTime("TH", "10:00", "16:30");
        setTime("TW", "09:30", "13:30");
        setTime("ID", "09:00", "15:00");
        setTime("TR", "09:00", "17:30");
        setTime("VN", "09:15", "14:30");
        setTime("LK", "10:30", "14:30");

        setTime("BR", "10:00", "17:30");
        setTime("CL", "09:30", "16:30");
        setTime("PA", "10:00", "15:00");
        setTime("AR", "11:00", "17:00");
        setTime("PE", "08:30", "15:00");
        setTime("CO", "09:30", "15:55");
    }
}