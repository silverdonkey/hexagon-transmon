package de.nikoconsulting.demo.hexagontransmon.app.port.in;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveMoneyValidator implements ConstraintValidator<PositiveMoney, Money> {

    @Override
    public boolean isValid(Money value, ConstraintValidatorContext context) {

        return value.isPositive();

    }

}
