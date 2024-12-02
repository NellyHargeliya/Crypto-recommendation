package org.task.crypto.dto;

import java.math.BigDecimal;

public record CryptoRange(String symbol, BigDecimal normalizedRange) {
}
