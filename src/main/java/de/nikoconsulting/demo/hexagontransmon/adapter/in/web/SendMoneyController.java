package de.nikoconsulting.demo.hexagontransmon.adapter.in.web;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Account;
import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import de.nikoconsulting.demo.hexagontransmon.app.port.in.SendMoneyCommand;
import de.nikoconsulting.demo.hexagontransmon.app.port.in.SendMoneyUseCase;
import de.nikoconsulting.demo.hexagontransmon.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A web adapter usually does these things:
 * 1. Map the incoming HTTP request to objects. => here performed automatically by Spring
 * 2. Perform authorization checks. => here not implemented for simplicity
 * 3. Validate the input. => performed in the Input-Model {@link SendMoneyCommand}
 * 4. Map the request objects to the input model of the use case. => simply taking the input and creating {@link SendMoneyCommand}
 * 5. Call the use case. => call {@link SendMoneyUseCase}
 * 6. Map the output of the use case back to HTTP. => not used here
 * 7. Return the HTTP response. => just forward to "success" or "error" page
 */
@WebAdapter
@RestController
@RequiredArgsConstructor
public class SendMoneyController {

    private final SendMoneyUseCase moneySender;

    @PostMapping(path = "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    void sendMoney(
            @PathVariable("sourceAccountId") Long sourceAccountId,
            @PathVariable("targetAccountId") Long targetAccountId,
            @PathVariable("amount") Long amount) {

        SendMoneyCommand command = new SendMoneyCommand(new Account.AccountId(sourceAccountId),
                new Account.AccountId(targetAccountId), Money.of(amount));

        moneySender.sendMoney(command);
    }
}
