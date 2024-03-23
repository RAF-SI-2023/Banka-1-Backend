package rs.edu.raf.banka1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Banka1Application {
	public static void main(String[] args) {
		SpringApplication.run(Banka1Application.class, args);
	}
}
