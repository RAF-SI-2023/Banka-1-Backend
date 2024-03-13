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
        parseExchangeCSV();
        initTimes();
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

                exchange.setOpenTime(containsCountryCode(marketOpenTimes, exchange.getCountry()));
                exchange.setCloseTime(containsCountryCode(marketCloseTimes, exchange.getCountry()));
                exchanges.add(exchange);
            }
            if (!exchanges.isEmpty()) {
                exchangeRepository.saveAll(exchanges);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    // Method to check if a country ISO code or its partial representation is contained in the map
    private String containsCountryCode(Map<String, String> map, String code) {
        for (String key : map.keySet()) {
            if (key.contains(code)) {
                return map.get(key);
            }
        }
        return "N/A";
    }

    //TODO dodati praznike za berze

    private void setTime(String country, String openTime, String closeTime) {
        marketOpenTimes.put(country, openTime);
        marketCloseTimes.put(country, closeTime);
    }

    private void initTimes() {
        setTime("NYSE", "09:30", "16:00");
        setTime("TSX", "09:30", "16:00");
        setTime("LSE", "08:00", "16:00");
        setTime("PSE-PRAGUE", "09:00", "16:20");
        setTime("FSX", "08:00", "20:00");
        setTime("XETRA", "09:00", "17:30");
        setTime("EUREX", "08:00", "22:00");
        setTime("CSE", "09:00", "17:00");
        setTime("OMXH", "10:00", "18:30");
        setTime("EPA", "09:00", "17:30");
        setTime("ISE", "08:00", "16:30");
        setTime("MTE", "09:00", "17:25");
        setTime("LuxSE", "09:00", "17:35");
        setTime("OSE", "09:00", "16:30");
        setTime("OMX", "09:00", "17:30");
        setTime("BME", "09:00", "17:30");
        setTime("SIX", "09:00", "17:30");
        setTime("BX", "09:00", "16:30");
        setTime("UX", "10:00", "17:30");
        setTime("WBAH", "08:55", "17:35");
        setTime("BSE-BULGARIA", "10:10", "16:55");
        setTime("ASE-ATHENS", "10:15", "17:20");
        setTime("WSE", "09:00", "17:35");
        setTime("BVB", "10:00", "17:45");
        setTime("MNSE", "09:30", "14:00");
        setTime("BSE-BUDAPEST", "09:00", "17:00");
        setTime("ZSE", "09:30", "15:55");

        setTime("ASX", "10:00", "16:00");
        setTime("SZSE", "09:30", "15:00");
        setTime("SSE", "09:30", "15:00");
        setTime("HKEX", "09:20", "16:00");
        setTime("NSE", "09:15", "15:30");
        setTime("BSE-BOMBAY", "09:15", "15:30");
        setTime("TSE", "09:00", "15:00");
        setTime("KRX", "09:00", "15:00");
        setTime("MYX", "09:00", "17:00");
        setTime("NZX", "10:00", "17:00");
        setTime("NZX", "10:00", "17:00");
        setTime("PSE-PHILIPPINE", "09:30", "15:30");
        setTime("SGX", "09:00", "17:00");
        setTime("SET", "10:00", "16:30");
        setTime("TWSE", "09:30", "13:30");
        setTime("IDX", "09:00", "15:00");
        setTime("BIST", "09:00", "17:30");
        setTime("HOSE", "09:15", "14:30");
        setTime("HNX", "09:00", "15:30");
        setTime("CSE-COLOMBO", "10:30", "14:30");

        setTime("BM&F", "10:00", "17:30");
        setTime("BVS", "09:30", "16:30");
        setTime("BVPA", "10:00", "15:00");
        setTime("BCBA", "11:00", "17:00");
        setTime("BVL", "08:30", "15:00");
        setTime("BVC", "09:30", "15:55");
    }
}