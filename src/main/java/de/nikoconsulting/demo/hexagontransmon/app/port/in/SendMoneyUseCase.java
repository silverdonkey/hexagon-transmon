package de.nikoconsulting.demo.hexagontransmon.app.port.in;

public interface SendMoneyUseCase {

    boolean sendMoney(SendMoneyCommand command);

}
