package org.task.crypto.dto;

import java.math.BigDecimal;

public record CryptoPriceDto(long timestamp, String symbol, BigDecimal price) {
}
