package de.nikoconsulting.demo.hexagontransmon.app.port.out;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;

import java.time.LocalDateTime;

public interface LoadAccount {

    Account loadAccount(Account.AccountId accountId, LocalDateTime baselineDate);

}
