package de.nikoconsulting.demo.hexagontransmon.app.port.in;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account.AccountId;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import jakarta.validation.constraints.NotNull;

import static de.nikoconsulting.demo.hexagontransmon.common.validation.Validation.validate;

/**
 * This is a DTO object and not a "command pattern" implementation!
 *
 * @param sourceAccountId
 * @param targetAccountId
 * @param money
 */
public record SendMoneyCommand(@NotNull AccountId sourceAccountId,
                               @NotNull  AccountId targetAccountId,
                               @NotNull @PositiveMoney Money money) {


    public SendMoneyCommand(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
        validate(this);
    }

}
