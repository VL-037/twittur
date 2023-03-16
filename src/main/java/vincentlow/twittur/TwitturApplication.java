package vincentlow.twittur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"vincentlow.twittur"})
public class TwitturApplication {

  public static void main(String[] args) {

    SpringApplication.run(TwitturApplication.class, args);
  }

}
