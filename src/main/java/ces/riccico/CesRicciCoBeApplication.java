package ces.riccico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CesRicciCoBeApplication {
	public static void main(String[] args) {
		SpringApplication.run(CesRicciCoBeApplication.class, args);
	}

}
