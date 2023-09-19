package de.nikoconsulting.demo.hexagontransmon.app.domain.service;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account.AccountId;
import de.nikoconsulting.demo.hexagontransmon.app.port.in.SendMoneyCommand;
import de.nikoconsulting.demo.hexagontransmon.app.port.in.SendMoneyUseCase;
import de.nikoconsulting.demo.hexagontransmon.app.port.out.AccountLock;
import de.nikoconsulting.demo.hexagontransmon.app.port.out.LoadAccount;
import de.nikoconsulting.demo.hexagontransmon.app.port.out.UpdateAccountState;
import de.nikoconsulting.demo.hexagontransmon.common.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * A UseCase usually does these things:
 * 1. Takes the input model. => {@link SendMoneyCommand}
 * 2. Validate the business rules. => checkThreshold method
 * 3. Manipulate the domain model state. => business logic
 * 4. Return the output. => true or false
 */
@RequiredArgsConstructor
@Transactional
@UseCase
public class MoneySender implements SendMoneyUseCase {
    private final LoadAccount loadAccountPort;
    private final AccountLock accountLock;
    private final UpdateAccountState updateAccountStatePort;
    private final MoneyTransferProperties moneyTransferProperties;

    @Override
    public boolean sendMoney(SendMoneyCommand command) {

        // validate business rules
        checkThreshold(command);

        // manipulate Account-model state
        LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);

        Account sourceAccount = loadAccountPort.loadAccount(
                command.sourceAccountId(),
                baselineDate);

        Account targetAccount = loadAccountPort.loadAccount(
                command.targetAccountId(),
                baselineDate);

        AccountId sourceAccountId = sourceAccount.getId()
                .orElseThrow(() -> new IllegalStateException("expected source account ID not to be empty"));
        AccountId targetAccountId = targetAccount.getId()
                .orElseThrow(() -> new IllegalStateException("expected target account ID not to be empty"));

        accountLock.lockAccount(sourceAccountId);
        if (!sourceAccount.withdraw(command.money(), targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            return false;
        }

        accountLock.lockAccount(targetAccountId);
        if (!targetAccount.deposit(command.money(), sourceAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            accountLock.releaseAccount(targetAccountId);
            return false;
        }

        updateAccountStatePort.updateActivities(sourceAccount);
        updateAccountStatePort.updateActivities(targetAccount);

        accountLock.releaseAccount(sourceAccountId);
        accountLock.releaseAccount(targetAccountId);

        // return output
        return true;
    }

    private void checkThreshold(SendMoneyCommand command) {
        if(command.money().isGreaterThan(moneyTransferProperties.getMaximumTransferThreshold())){
            throw new ThresholdExceededException(moneyTransferProperties.getMaximumTransferThreshold(), command.money());
        }
    }

}
