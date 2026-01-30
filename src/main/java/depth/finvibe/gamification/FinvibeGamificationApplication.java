package depth.finvibe.gamification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableKafka
@EnableJpaAuditing
@EnableAsync
@ConfigurationPropertiesScan
public class FinvibeGamificationApplication {

  public static void main(String[] args) {
    SpringApplication.run(FinvibeGamificationApplication.class, args);
  }

}
