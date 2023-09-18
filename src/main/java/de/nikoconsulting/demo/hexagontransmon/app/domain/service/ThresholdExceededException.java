package de.nikoconsulting.demo.hexagontransmon.app.domain.service;

import de.nikoconsulting.demo.hexagontransmon.app.domain.model.Money;

public class ThresholdExceededException extends RuntimeException {
    public ThresholdExceededException(Money threshold, Money actual) {
        super(String.format("Maximum threshold for transferring money exceeded: tried to transfer %s but threshold is %s!", actual, threshold));
    }

}
