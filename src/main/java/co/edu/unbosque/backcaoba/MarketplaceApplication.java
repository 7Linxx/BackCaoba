package co.edu.unbosque.backcaoba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class MarketplaceApplication {

    private MarketplaceApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceApplication.class, args);
    }
}
