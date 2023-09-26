package de.nikoconsulting.demo.hexagontransmon.adapter.out.persistence;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.ActivityWindow;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static de.nikoconsulting.demo.hexagontransmon.app.domain.model.AccountTestData.defaultAccount;
import static de.nikoconsulting.demo.hexagontransmon.app.domain.model.ActivityTestData.defaultActivity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * With @DataJpaTest, we tell Spring to instantiate the network of objects that are needed for database
 * access, including our Spring Data repositories that connect to the database. We add some additional
 * @Imports to make sure that certain objects are added to that network. These objects are needed by
 * the adapter under test to map incoming domain objects into database objects, for instance.
 *
 * We’re not mocking away the database!!! The tests actually
 * hit the database!
 *
 * Note that, by default, Spring will spin up an in-memory database to use during tests. This is very
 * practical, as we don’t have to configure anything, and the tests will work out of the box. However,
 * since this in-memory database is most probably not the database we use in production, there is still
 * a significant chance of something going wrong with the real database even when the tests work
 * perfectly against the in-memory database. Database vendors love to implement their own flavor of
 * SQL, for instance.
 *
 * For this reason, persistence adapter tests should run against the real database. Libraries such as
 * Testcontainers are a great help in this regard, spinning up a Docker container with a database on
 * demand.
 */
@DataJpaTest
@Import({AccountPersistenceAdapter.class, AccountMapper.class})
@Testcontainers
@TestPropertySource(properties = {
        "spring.test.database.replace=none", // turn-off the autoconfiguration of DataSource bean, which by default uses in-memory database
        "spring.jpa.hibernate.ddl-auto=create"
})
class AccountPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> DATABASE =
            new PostgreSQLContainer<>("postgres:15.2-alpine");
    @Autowired
    private AccountPersistenceAdapter adapterUnderTest;

    @Autowired
    private ActivityRepository activityRepository;

    @Test
    @Sql("AccountPersistenceAdapterTest.sql")
    void loadsAccount() {
        Account account = adapterUnderTest.loadAccount(new Account.AccountId(1L), LocalDateTime.of(2018, 8, 10, 0, 0));

        assertThat(account.getActivityWindow().getActivities()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }


    @Test
    void updatesActivities() {
        Account account = defaultAccount()
                .withBaselineBalance(Money.of(555L))
                .withActivityWindow(new ActivityWindow(
                        defaultActivity()
                                .withId(null)
                                .withMoney(Money.of(1L)).build()))
                .build();

        adapterUnderTest.updateActivities(account);

        assertThat(activityRepository.count()).isEqualTo(1);

        ActivityJpaEntity savedActivity = activityRepository.findAll().get(0);
        assertThat(savedActivity.getAmount()).isEqualTo(1L);
    }

}