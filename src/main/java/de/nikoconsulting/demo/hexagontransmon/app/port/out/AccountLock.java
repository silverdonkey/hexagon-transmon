package de.nikoconsulting.demo.hexagontransmon.app.port.out;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;

public interface AccountLock {

    void lockAccount(Account.AccountId accountId);

    void releaseAccount(Account.AccountId accountId);

}
