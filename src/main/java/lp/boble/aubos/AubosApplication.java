package lp.boble.aubos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AubosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AubosApplication.class, args);
    }

}
