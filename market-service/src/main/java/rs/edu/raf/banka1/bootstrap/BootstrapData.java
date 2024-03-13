package rs.edu.raf.banka1.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

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
