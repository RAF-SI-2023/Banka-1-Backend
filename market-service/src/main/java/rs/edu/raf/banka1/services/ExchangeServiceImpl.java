package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.utils.Constants;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    @Override
    public void seedDatabase() {
        Map<String, BusinessHours> resultMap = parseJson();
        parseCsv(resultMap);

    }

    private void parseCsv(Map<String, BusinessHours> resultMap) {
        try (CSVReader reader = new CSVReader(new FileReader(Constants.micCsvFilePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                System.out.println(nextLine[0]);

                System.out.println();
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, BusinessHours> parseJson() {
        Map<String, BusinessHours> resultMap = null;
        try {
            // Create an ObjectMapper instance
            ObjectMapper mapper = new ObjectMapper();

            // Read the JSON file and parse it into a Map<String, Object>
            resultMap = mapper.readValue(new File(Constants.businessHoursFilePath), HashMap.class);

            // Accessing fields from the parsed JSON
            for (Map.Entry<String, BusinessHours> entry : resultMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @Getter
    @Setter
    @ToString
    private static class BusinessHours {
        private String open;
        private String close;
        private List<String> holidays;
    }
}
