package de.nikoconsulting.demo.hexagontransmon.app.port.out;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;

public interface UpdateAccountState {

    void updateActivities(Account account);

}
