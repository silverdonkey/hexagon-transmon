package de.nikoconsulting.demo.hexagontransmon.app.domain.service;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account.AccountId;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import de.nikoconsulting.demo.hexagontransmon.app.port.in.SendMoneyCommand;
import de.nikoconsulting.demo.hexagontransmon.app.port.out.AccountLock;
import de.nikoconsulting.demo.hexagontransmon.app.port.out.LoadAccount;
import de.nikoconsulting.demo.hexagontransmon.app.port.out.UpdateAccountState;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class MoneySenderTest {

    private final LoadAccount loadAccountPort = Mockito.mock(LoadAccount.class);
    private final AccountLock accountLock = Mockito.mock(AccountLock.class);
    private final UpdateAccountState updateAccountStatePort = Mockito.mock(UpdateAccountState.class);

    // service under test
    private final MoneySender sendMoneyService = new MoneySender(loadAccountPort, accountLock, updateAccountStatePort, moneyTransferProperties());

    // mock the props
    private MoneyTransferProperties moneyTransferProperties(){
        return new MoneyTransferProperties(Money.of(Long.MAX_VALUE));
    }

    @Test
    void sendMondeyTransactionSucceeds() {

        Account sourceAccount = givenSourceAccount();
        Account targetAccount = givenTargetAccount();

        givenWithdrawalWillSucceed(sourceAccount);
        givenDepositWillSucceed(targetAccount);

        Money money = Money.of(500L);

        SendMoneyCommand command = new SendMoneyCommand(
                sourceAccount.getId().get(),
                targetAccount.getId().get(),
                money);

        boolean success = sendMoneyService.sendMoney(command);

        assertThat(success).isTrue();

        AccountId sourceAccountId = sourceAccount.getId().get();
        AccountId targetAccountId = targetAccount.getId().get();

        then(accountLock).should().lockAccount(eq(sourceAccountId));
        then(sourceAccount).should().withdraw(eq(money), eq(targetAccountId));
        then(accountLock).should().releaseAccount(eq(sourceAccountId));

        then(accountLock).should().lockAccount(eq(targetAccountId));
        then(targetAccount).should().deposit(eq(money), eq(sourceAccountId));
        then(accountLock).should().releaseAccount(eq(targetAccountId));

        thenAccountsHaveBeenUpdated(sourceAccountId, targetAccountId);
    }


    @Test
    void givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {

        AccountId sourceAccountId = new AccountId(41L);
        Account sourceAccount = givenAnAccountWithId(sourceAccountId);

        AccountId targetAccountId = new AccountId(42L);
        Account targetAccount = givenAnAccountWithId(targetAccountId);

        givenWithdrawalWillFail(sourceAccount);
        givenDepositWillSucceed(targetAccount);

        SendMoneyCommand command = new SendMoneyCommand(
                sourceAccountId,
                targetAccountId,
                Money.of(300L));

        boolean success = sendMoneyService.sendMoney(command);

        assertThat(success).isFalse();

        then(accountLock).should().lockAccount(eq(sourceAccountId));
        then(accountLock).should().releaseAccount(eq(sourceAccountId));
        then(accountLock).should(times(0)).lockAccount(eq(targetAccountId));
    }


    // helper methods

    // Behavior-Driven Development BDD
    private void thenAccountsHaveBeenUpdated(AccountId... accountIds){
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        then(updateAccountStatePort).should(times(accountIds.length))
                .updateActivities(accountCaptor.capture());

        List<AccountId> updatedAccountIds = accountCaptor.getAllValues()
                .stream()
                .map(Account::getId)
                .map(Optional::get)
                .collect(Collectors.toList());

        for(AccountId accountId : accountIds){
            assertThat(updatedAccountIds).contains(accountId);
        }
    }

    private void givenDepositWillSucceed(Account account) {
        given(account.deposit(any(Money.class), any(AccountId.class)))
                .willReturn(true);
    }

    private void givenWithdrawalWillFail(Account account) {
        given(account.withdraw(any(Money.class), any(AccountId.class)))
                .willReturn(false);
    }

    private void givenWithdrawalWillSucceed(Account account) {
        given(account.withdraw(any(Money.class), any(AccountId.class)))
                .willReturn(true);
    }

    private Account givenTargetAccount(){
        return givenAnAccountWithId(new AccountId(42L));
    }

    private Account givenSourceAccount(){
        return givenAnAccountWithId(new AccountId(41L));
    }

    private Account givenAnAccountWithId(AccountId id) {
        Account account = Mockito.mock(Account.class);
        given(account.getId())
                .willReturn(Optional.of(id));
        given(loadAccountPort.loadAccount(eq(account.getId().get()), any(LocalDateTime.class)))
                .willReturn(account);
        return account;
    }


}