package de.nikoconsulting.demo.hexagontransmon;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account.AccountId;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import de.nikoconsulting.demo.hexagontransmon.app.port.out.LoadAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * With @SpringBootTest, we tell Spring to start up the whole network of objects that make up the
 * application. We also configure the application to expose itself on a random port.
 * In the test method, we simply create a request, send it to the application, and then check the response
 * status and the new balance of the accounts.
 * We use a TestRestTemplate to send the request, and not MockMvc, as we did earlier in the web adapter
 * test. This means that the tests makes real HTTP calls, bringing the test a little closer to a production
 * environment.
 *
 * In addition, this System test runs against the real database - spinning up a Testcontainer with Postgres Server.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
        "spring.test.database.replace=none",
        "spring.jpa.hibernate.ddl-auto=create"
})
public class SendMoneySystemTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> DATABASE =
            new PostgreSQLContainer<>("postgres:15.2-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LoadAccount loadAccountPort;

    @Test
    @Sql("SendMoneySystemTest.sql")
    void sendMoney() {

        Money initialSourceBalance = sourceAccount().calculateBalance();
        Money initialTargetBalance = targetAccount().calculateBalance();

        ResponseEntity response = whenSendMoney(
                sourceAccountId(),
                targetAccountId(),
                transferredAmountOf500());

        then(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        then(sourceAccount().calculateBalance())
                .isEqualTo(initialSourceBalance.minus(transferredAmountOf500()));

        then(targetAccount().calculateBalance())
                .isEqualTo(initialTargetBalance.plus(transferredAmountOf500()));

    }

    // helper methods

    private ResponseEntity whenSendMoney(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        return restTemplate.exchange(
                "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                HttpMethod.POST,
                request,
                Object.class,
                sourceAccountId.getValue(),
                targetAccountId.getValue(),
                amount.getAmount());
    }

    private Account sourceAccount() {
        return loadAccount(sourceAccountId());
    }

    private Account targetAccount() {
        return loadAccount(targetAccountId());
    }

    private Account loadAccount(AccountId accountId) {
        return loadAccountPort.loadAccount(
                accountId,
                LocalDateTime.now());
    }

    private Money transferredAmountOf500() {
        return Money.of(500L);
    }

    private Money balanceOf(AccountId accountId) {
        Account account = loadAccountPort.loadAccount(accountId, LocalDateTime.now());
        return account.calculateBalance();
    }

    private AccountId sourceAccountId() {
        return new AccountId(1L);
    }

    private AccountId targetAccountId() {
        return new AccountId(2L);
    }
}
