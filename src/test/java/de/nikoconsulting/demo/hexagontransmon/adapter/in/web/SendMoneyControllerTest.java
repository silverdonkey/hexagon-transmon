package de.nikoconsulting.demo.hexagontransmon.adapter.in.web;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account.AccountId;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import de.nikoconsulting.demo.hexagontransmon.app.port.in.SendMoneyCommand;
import de.nikoconsulting.demo.hexagontransmon.app.port.in.SendMoneyUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * This is an Integration-Test!
 * With the @WebMvcTest
 * annotation we tell Spring to instantiate a whole network of objects that is responsible for responding
 * to certain request paths, mapping between Java and JSON, validating HTTP input, and so on. And
 * in this test, we verify that our web controller works as a part of this network.
 *
 * Since the web controller is heavily coupled to the Spring framework, it makes sense to test it when
 * integrated into this framework instead of testing it in isolation. If we tested the web controller with
 * a plain unit test, we’d lose coverage of all the mapping, validation, and HTTP stuff, and we could
 * never be sure whether it actually worked in production, where it’s just a cog in the mechanics of
 * the framework.
 *
 * We’re not actually testing via the HTTP protocol, since we’re mocking that away with the MockMvc
 * object. We trust that the framework translates everything to and from HTTP properly. There is no
 * need to test the framework.
 *
 * However, the whole path from mapping the input from JSON into a SendMoneyCommand object is
 * covered. If we build the SendMoneyCommand object as a self-validating command we even make sure that this mapping produces syntactically
 * valid input to the use case {@link SendMoneyUseCase}. Also, we have verified that the use case is actually called, and that the
 * HTTP response has the expected status
 *
 */
@WebMvcTest(controllers = SendMoneyController.class)
class SendMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendMoneyUseCase moneySenderMock;

    @Test
    void sendMoney() throws Exception {
        mockMvc.perform(post("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                        41L, 42L, 500)
                        .header("Content-Type", "application/json"))
                .andExpect(status().isOk());

        then(moneySenderMock).should()
                .sendMoney(eq(new SendMoneyCommand(
                        new AccountId(41L),
                        new AccountId(42L),
                        Money.of(500L))));
    }
}