package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.pmw.tinylog.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.dtos.BusinessHoursDto;
import rs.edu.raf.banka1.model.dtos.CountryTimezoneDto;
import rs.edu.raf.banka1.model.entities.Country;
import rs.edu.raf.banka1.model.entities.Exchange;
import rs.edu.raf.banka1.model.entities.Holiday;
import rs.edu.raf.banka1.repositories.CountryRepository;
import rs.edu.raf.banka1.repositories.ExchangeRepository;
import rs.edu.raf.banka1.repositories.HolidayRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static rs.edu.raf.banka1.utils.Constants.*;

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
        CountryTimezoneDto[] countryTimezones = parseCountryTimezonesJson();
        // mapping from iso to country entity
        Map<String, Country> countryIsoToCountryMap = getCountryMap(countryTimezones);
        // save countries to database
        countryIsoToCountryMap.replaceAll((k, v) -> countryRepository.save(v));
        parseCsv(countryIsoToBusinessHoursMap, countryIsoToCountryMap);
    }

    private void parseCsv(Map<String, BusinessHoursDto> countryIsoToBusinessHoursMap, Map<String, Country> countryIsoToCountryMap) {
        CSVReader reader = null;
        Resource resource = null;
//        try (CSVReader reader = new CSVReader(new FileReader(Constants.micCsvFilePath))) {
        try {

            resource = new ClassPathResource(micCsvFilePath, this.getClass().getClassLoader());
            InputStream in = resource.getInputStream();
            reader = new CSVReader(new InputStreamReader(in));

            // e.g. 17:00:00
            SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm:ss");
            // e.g. 2024-01-01
            SimpleDateFormat dateDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Map<String, Set<Date>> countryIsoToAllHolidayDatesMap = new HashMap<>();
            // initialize map values to empty set for every country
            countryIsoToCountryMap.keySet()
                    .forEach(countryIso -> countryIsoToAllHolidayDatesMap.put(countryIso, new HashSet<>()));

            String[] nextLine;
            //skip header
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                String countryIso = nextLine[8];
                String micCode = nextLine[0];

                Country country = countryIsoToCountryMap.get(countryIso);
                // happens only when unknown country
                if (country == null) {
                    country = new Country();
                    country.setISOCode(countryIso);
                    country.setTimezoneOffset(0);
                    countryIsoToCountryMap.put(countryIso, countryRepository.save(country));
                }
                BusinessHoursDto businessHours = countryIsoToBusinessHoursMap.get(micCode);

                if (businessHours != null) {
                    // set open and close times for country based on it's first occurrence in the csv
                    if (country.getCloseTime() == null) {
                        country.setOpenTime(hoursDateFormat.parse(businessHours.getOpen()));
                        country.setCloseTime(hoursDateFormat.parse(businessHours.getClose()));
                        country = countryRepository.save(country);
                        countryIsoToCountryMap.put(countryIso, country);
                    }
                    for (String holidayString : businessHours.getHolidays()) {
                        Date date = dateDateFormat.parse(holidayString);
                        countryIsoToAllHolidayDatesMap.get(countryIso).add(date);
                    }
                }
                saveExchange(micCode, nextLine[3], nextLine[7], country);
            }
            saveAllHolidays(countryIsoToCountryMap, countryIsoToAllHolidayDatesMap);
        } catch (IOException | CsvValidationException | ParseException e) {
            e.printStackTrace();
            Logger.error("[ExchangeSericeImpl] Caught exception during " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Logger.error("[-] Error occured when trying to read file in exchange service impl " + e.getMessage());
            }
        }
    }

    private void saveExchange(String micCode, String exchangeName, String exchangeAcronym, Country country) {
        Exchange exchange = new Exchange();
        exchange.setMicCode(micCode);
        exchange.setExchangeName(exchangeName);
        exchange.setExchangeAcronym(exchangeAcronym);
        exchange.setCountry(country);
        exchangeRepository.save(exchange);
    }

    private void saveAllHolidays(Map<String, Country> countryIsoToCountryMap, Map<String, Set<Date>> countryIsoToAllHolidayDatesMap) {
        for (Map.Entry<String, Set<Date>> entry : countryIsoToAllHolidayDatesMap.entrySet()) {
            Country country = countryIsoToCountryMap.get(entry.getKey());
            for (Date date : entry.getValue()) {
                Holiday holiday = new Holiday();
                holiday.setDate(date);
                holiday.setCountry(country);
                holidayRepository.save(holiday);
            }
        }
    }

    private Map<String, Country> getCountryMap(CountryTimezoneDto[] countryTimezones) {
        Map<String, Country> countryIsoToCountryMap = new HashMap<>();
        for (CountryTimezoneDto ct : countryTimezones) {
            if (!countryIsoToCountryMap.containsKey(ct.getCountryCode())) {
                Country country = new Country();
                country.setISOCode(ct.getCountryCode());
                country.setTimezoneOffset(ct.getGmtOffset());
                countryIsoToCountryMap.put(ct.getCountryCode(), country);
            }
        }
        return countryIsoToCountryMap;
    }

    private CountryTimezoneDto[] parseCountryTimezonesJson() {
        CountryTimezoneDto[] countryTimezones = null;
        Resource resource = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            resource = new ClassPathResource(countryTimezoneOffsetsFilePath, this.getClass().getClassLoader());
            countryTimezones = mapper.readValue(resource.getInputStream(), CountryTimezoneDto[].class);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("[ExchangeServiceImpl] Caught exception " + e.getMessage());
        }

        return countryTimezones;
    }

    private Map<String, BusinessHoursDto> parseBusinessHoursJson() {
        Map<String, BusinessHoursDto> resultMap = null;
        Resource resource = null;

        try {

            ObjectMapper mapper = new ObjectMapper();
            resource = new ClassPathResource(businessHoursFilePath, this.getClass().getClassLoader());
            resultMap = mapper.readValue(resource.getInputStream(), HashMap.class);

            for (Map.Entry<String, BusinessHoursDto> entry : resultMap.entrySet()) {
                resultMap.put(entry.getKey(), mapper.convertValue(entry.getValue(), BusinessHoursDto.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("[ExchangeServiceImpl] Caught exception " + e.getMessage());
        }

        return resultMap;
    }
}
