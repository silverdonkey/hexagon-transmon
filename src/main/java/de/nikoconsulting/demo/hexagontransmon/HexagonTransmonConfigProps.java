package de.nikoconsulting.demo.hexagontransmon;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hexagontransmon")
public class HexagonTransmonConfigProps {

    private long transferThreshold = Long.MAX_VALUE;

}
