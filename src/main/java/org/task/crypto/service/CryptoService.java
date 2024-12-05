package org.task.crypto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.task.crypto.dto.CryptoRange;
import org.task.crypto.exception.NoContentException;
import org.task.crypto.model.CryptoPrice;
import org.task.crypto.repository.CryptoPriceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoPriceRepository cryptoPriceRepository;

    @Cacheable(value = "cryptoPrices", key = "#symbol")
    public List<CryptoPrice> getCryptoPrices(String symbol) {
        return cryptoPriceRepository.findBySymbol(symbol);
    }

    public CryptoPrice getOldestPrice(String symbol) {
        return cryptoPriceRepository.findBySymbol(symbol).stream()
                .min(Comparator.comparing(CryptoPrice::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new NoContentException("No data found for symbol: " + symbol));
    }

    public CryptoPrice getNewestPrice(String symbol) {
        return cryptoPriceRepository.findBySymbol(symbol).stream()
                .max(Comparator.comparing(CryptoPrice::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new NoContentException("No data found for symbol: " + symbol));
    }

    public BigDecimal getMaxPrice(String symbol, Integer months) {
        return getPrice(symbol, months, Comparator.naturalOrder());
    }

    public BigDecimal getMinPrice(String symbol, Integer months) {
        return getPrice(symbol, months, Comparator.reverseOrder());
    }

    private BigDecimal getPrice(String symbol, Integer months, Comparator<BigDecimal> comparator) {
        int effectiveMonths = Optional.ofNullable(months).orElse(1);
        LocalDateTime startDate = calculateStartDate(effectiveMonths);
        LocalDateTime endDate = LocalDateTime.now();

        List<CryptoPrice> prices = cryptoPriceRepository.findBySymbolAndTimestampBetween(symbol, startDate, endDate);

        return prices.stream()
                .map(CryptoPrice::getPrice)
                .max(comparator)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateNormalizedRange(String symbol, LocalDateTime start, LocalDateTime end) {
        List<CryptoPrice> prices = cryptoPriceRepository.findBySymbolAndTimestampBetween(symbol, start, end);

        if (prices.isEmpty()) {
            throw new NoContentException("No price data available for the specified period.");
        }

        BigDecimal minPrice = prices.stream()
                .map(CryptoPrice::getPrice)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new NoContentException("No data found for symbol: " + symbol));

        BigDecimal maxPrice = prices.stream()
                .map(CryptoPrice::getPrice)
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new NoContentException("No data found for symbol: " + symbol));

        if (minPrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Minimum price cannot be zero for normalization calculation.");
        }

        return (maxPrice.subtract(minPrice)).divide(minPrice, RoundingMode.HALF_UP);
    }

    public List<CryptoRange> getCryptosSortedByNormalizedRange(LocalDateTime start, LocalDateTime end) {
        List<CryptoPrice> allPrices = cryptoPriceRepository.findByTimestampBetween(start, end);

        Map<String, List<CryptoPrice>> groupedBySymbol = allPrices.stream()
                .collect(Collectors.groupingBy(CryptoPrice::getSymbol));

        return groupedBySymbol.entrySet().stream()
                .map(entry -> calculateNormalizedRangeForSymbol(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CryptoRange::normalizedRange).reversed())
                .toList();
    }

    private CryptoRange calculateNormalizedRangeForSymbol(String symbol, List<CryptoPrice> prices) {
        BigDecimal minPrice = prices.stream()
                .map(CryptoPrice::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = prices.stream()
                .map(CryptoPrice::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal normalizedRange = maxPrice.subtract(minPrice).divide(minPrice, RoundingMode.HALF_UP);
        return new CryptoRange(symbol, normalizedRange);
    }


    public CryptoPrice getCryptoWithHighestNormalizedRange(LocalDateTime day) {
        LocalDateTime startOfDay = day.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<CryptoPrice> allPrices = cryptoPriceRepository.findByTimestampBetween(startOfDay, endOfDay);

        Map<String, List<CryptoPrice>> groupedBySymbol = allPrices.stream()
                .collect(Collectors.groupingBy(CryptoPrice::getSymbol));

        return groupedBySymbol.entrySet().stream()
                .map(entry -> calculateNormalizedRangeForSymbol(entry.getKey(), entry.getValue()))
                .max(Comparator.comparing(CryptoRange::normalizedRange))
                .map(range -> cryptoPriceRepository.findBySymbol(range.symbol()).getFirst())
                .orElseThrow(() -> new NoContentException("No data available for the given day"));
    }

    private LocalDateTime calculateStartDate(int months) {
        return LocalDateTime.now().minusMonths(months);
    }
}
