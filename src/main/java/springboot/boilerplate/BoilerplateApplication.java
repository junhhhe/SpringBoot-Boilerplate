package springboot.boilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springboot.boilerplate.global.config.EnvConfig;

@EnableJpaAuditing
@SpringBootApplication
public class BoilerplateApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(BoilerplateApplication.class);
		application.addInitializers(new EnvConfig());
		application.run(args);
	}

}
