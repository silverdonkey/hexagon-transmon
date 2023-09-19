package de.nikoconsulting.demo.hexagontransmon;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import de.nikoconsulting.demo.hexagontransmon.app.domain.service.MoneyTransferProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HexagonTransmonConfigProps.class)
public class HexagonTransmonConfiguration {

    /**
     * Adds a use-case-specific {@link MoneyTransferProperties} object to the application context. The properties
     * are read from the Spring-Boot-specific {@link HexagonTransmonConfigProps} object.
     */
    @Bean
    public MoneyTransferProperties moneyTransferProperties(HexagonTransmonConfigProps hexagonTransmonConfigProps){
        return new MoneyTransferProperties(Money.of(hexagonTransmonConfigProps.getTransferThreshold()));
    }

}
