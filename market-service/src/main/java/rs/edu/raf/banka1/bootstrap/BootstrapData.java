package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.services.CurrencyService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final CurrencyService currencyService;

    @Override
    public void run(String... args) {
        System.out.println("Loading Data...");

        List<CurrencyDto> currencyList = loadCurrencies();
        currencyService.addCurrencies(currencyList);
        System.out.println("Currency Data Loaded!");

        System.out.println("All Data loaded!");
    }

    public List<CurrencyDto> loadCurrencies() {
        List<CurrencyDto> currencyList = new ArrayList<>();
        String csvFile = "market-service/src/main/resources/physical_currency_list.csv";
        String line = "";
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] currencyData = line.split(csvSplitBy);
                if (currencyData.length == 2) {
                    String code = currencyData[0].trim();
                    String name = currencyData[1].trim();
                    currencyList.add(new CurrencyDto(name, code));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currencyList;
    }
}
