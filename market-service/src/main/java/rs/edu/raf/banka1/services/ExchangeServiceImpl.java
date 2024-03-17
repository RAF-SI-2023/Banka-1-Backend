package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.entities.Country;
import rs.edu.raf.banka1.model.entities.Exchange;
import rs.edu.raf.banka1.model.entities.Holiday;
import rs.edu.raf.banka1.repositories.CountryRepository;
import rs.edu.raf.banka1.repositories.ExchangeRepository;
import rs.edu.raf.banka1.repositories.HolidayRepository;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.DateUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;
    private final ExchangeRepository exchangeRepository;

    @Autowired
    public ExchangeServiceImpl(
            CountryRepository countryRepository,
            HolidayRepository holidayRepository,
            ExchangeRepository exchangeRepository) {
        this.countryRepository = countryRepository;
        this.holidayRepository = holidayRepository;
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public void seedDatabase() {
        Map<String, BusinessHoursDto> countryIsoToBusinessHoursMap = parseBusinessHoursJson();
        Map<String, Country> countryIsoToCountryMap = parseCountryTimezonesJson();
        assert countryIsoToCountryMap != null;
        saveCountriesToDatabase(countryIsoToCountryMap);
        System.out.println(countryIsoToBusinessHoursMap);
        System.out.println(countryIsoToCountryMap);
        parseCsv(countryIsoToBusinessHoursMap, countryIsoToCountryMap);

    }

    private void saveCountriesToDatabase(Map<String, Country> countryIsoToBusinessHoursMap) {
        for (Map.Entry<String, Country> entry : countryIsoToBusinessHoursMap.entrySet()) {
            Country country = countryRepository.save(entry.getValue());
            countryIsoToBusinessHoursMap.put(entry.getKey(), country);
        }
    }

    private void parseCsv(Map<String, BusinessHoursDto> countryIsoToBusinessHoursMap, Map<String, Country> countryIsoToCountryMap) {
        try (CSVReader reader = new CSVReader(new FileReader(Constants.micCsvFilePath))) {
            SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Map<String, Set<Date>> countryIsoToAllHolidayDatesMap = new HashMap<>();
            countryIsoToCountryMap.keySet().forEach(countryIso -> countryIsoToAllHolidayDatesMap.put(countryIso, new HashSet<>()));

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Exchange exchange = new Exchange();
                String countryIso = nextLine[8];
                String micCode = nextLine[0];

                exchange.setMicCode(micCode);
                exchange.setExchangeName(nextLine[3]);
                exchange.setExchangeAcronym(nextLine[7]);


                System.out.println("===========");
                System.out.println(countryIso);
                System.out.println(countryIsoToCountryMap.get(countryIso));
                System.out.println(micCode);
                System.out.println(countryIsoToBusinessHoursMap.get(micCode));
                System.out.println("--------------");

                Country country = countryIsoToCountryMap.get(countryIso);
                BusinessHoursDto businessHours = countryIsoToBusinessHoursMap.get(micCode);

                if (businessHours != null) {
                    System.out.println("businessHours");
                    if (country.getCloseTime() == null) {
                        country.setOpenTime(hoursDateFormat.parse(businessHours.getOpen()));
                        country.setCloseTime(hoursDateFormat.parse(businessHours.getClose()));
                        countryIsoToCountryMap.put(countryIso, countryRepository.save(country));
                    }

                    for(String holidayString : businessHours.getHolidays()) {
                        Date date = dateDateFormat.parse(holidayString);
                        countryIsoToAllHolidayDatesMap.get(countryIso).add(date);
                    }
                }
//
//                exchange.setCountry();
//                exchange.setCurrency();

            }

            for (Map.Entry<String, Set<Date>> entry : countryIsoToAllHolidayDatesMap.entrySet()) {
                Country country = countryIsoToCountryMap.get(entry.getKey());
                for (Date date : entry.getValue()) {
                    Holiday holiday = new Holiday();
                    holiday.setDate(date);
                    holiday.setCountry(country);
                    holidayRepository.save(holiday);
                }
            }
        } catch (IOException | CsvValidationException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Country> parseCountryTimezonesJson() {
        CountryTimezoneDto[] countryTimezones = null;
        try {
            // Create an ObjectMapper instance
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Read the JSON file and parse it into a Map<String, CountryTimezone>
            countryTimezones = mapper.readValue(new File(Constants.countryTimezoneOffsetsFilePath), CountryTimezoneDto[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(countryTimezones == null) return null;

        Map<String, Country> countryIsoToCountryMap = new HashMap<>();
        for(CountryTimezoneDto ct : countryTimezones) {
                if (!countryIsoToCountryMap.containsKey(ct.countryCode)) {
                    Country country = new Country();
                    country.setISOCode(ct.countryCode);
                    country.setTimezoneOffset(ct.gmtOffset);
                    countryRepository.save(country);
                    countryIsoToCountryMap.put(ct.countryCode, country);
                }
        }

        return countryIsoToCountryMap;
    }

    private static Map<String, BusinessHoursDto> parseBusinessHoursJson() {
        Map<String, BusinessHoursDto> resultMap = null;
        try {
            // Create an ObjectMapper instance
            ObjectMapper mapper = new ObjectMapper();

            // Read the JSON file and parse it into a Map<String, Object>
            resultMap = mapper.readValue(new File(Constants.businessHoursFilePath), HashMap.class);


            // Accessing fields from the parsed JSON
            for (Map.Entry<String, BusinessHoursDto> entry : resultMap.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
                resultMap.put(entry.getKey(), mapper.convertValue(entry.getValue(), BusinessHoursDto.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @Getter
    @Setter
    @ToString
    private static class BusinessHoursDto {
        private String open;
        private String close;
        private List<String> holidays;
    }

    @Getter
    @Setter
    @ToString
    private static class CountryTimezoneDto {
        private String countryCode;
        private Integer gmtOffset;
    }
}
