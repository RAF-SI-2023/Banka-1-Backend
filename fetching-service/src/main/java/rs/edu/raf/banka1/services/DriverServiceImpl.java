package rs.edu.raf.banka1.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

@Service
public class DriverServiceImpl implements DriverService{

    //private WebDriver driver;
    private final ChromeOptions options;
    public DriverServiceImpl() {
        options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
//        options.setExperimentalOption("images", 2);
    }

    @Override
    public WebDriver createNewDriver() {
        return new ChromeDriver(options);
    }
}
