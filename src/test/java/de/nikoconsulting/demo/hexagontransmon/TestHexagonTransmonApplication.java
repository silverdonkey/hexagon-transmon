package de.nikoconsulting.demo.hexagontransmon;

import org.springframework.boot.SpringApplication;

public class TestHexagonTransmonApplication {

	public static void main(String[] args) {

		SpringApplication
				.from(HexagonTransmonApplication::main)
				.with(ContainerConfiguration.class)
				.run(args);
	}

}
