package sosohappy.dmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;
import sosohappy.dmservice.domain.collection.Message;

@SpringBootApplication
public class DmServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(DmServiceApplication.class, args);

	}
}
