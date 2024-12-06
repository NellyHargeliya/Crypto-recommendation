package org.task.crypto.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.task.crypto.dto.CryptoRange;
import org.task.crypto.exception.NoContentException;
import org.task.crypto.model.CryptoPrice;
import org.task.crypto.repository.CryptoPriceRepository;
import org.task.crypto.service.CryptoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class CryptoServiceTest {

    @Mock
    private CryptoPriceRepository mockRepository;

    @InjectMocks
    private CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCryptoPrices_symbolWithPrices_returnsPrices() {
        String symbol = "BTC";
        List<CryptoPrice> expectedPrices = Arrays.asList(new CryptoPrice(), new CryptoPrice());
        when(mockRepository.findBySymbol(symbol)).thenReturn(expectedPrices);

        List<CryptoPrice> result = cryptoService.getCryptoPrices(symbol);

        assertEquals(expectedPrices, result);
        verify(mockRepository).findBySymbol(symbol);
    }

    @Test
    void testGetCryptoPrices_symbolWithNoPrices_returnsEmptyList() {
        String symbol = "BTC";
        when(mockRepository.findBySymbol(symbol)).thenReturn(List.of());

        List<CryptoPrice> result = cryptoService.getCryptoPrices(symbol);

        assertTrue(result.isEmpty());
        verify(mockRepository).findBySymbol(symbol);
    }

    @Test
    void testGetOldestPrice_noPrices_throwsException() {
        String symbol = "BTC";
        when(mockRepository.findBySymbol(symbol)).thenReturn(List.of());

        assertThrows(NoContentException.class, () -> cryptoService.getOldestPrice(symbol));
        verify(mockRepository).findBySymbol(symbol);
    }

    @Test
    void testGetNewestPrice_withMultiplePrices_returnsNewest() {
        String symbol = "BTC";
        CryptoPrice newest = new CryptoPrice();
        newest.setTimestamp(LocalDateTime.now());

        CryptoPrice anotherPrice = new CryptoPrice();
        anotherPrice.setTimestamp(LocalDateTime.now().minusDays(1));

        List<CryptoPrice> prices = List.of(anotherPrice, newest);
        when(mockRepository.findBySymbol(symbol)).thenReturn(prices);

        CryptoPrice result = cryptoService.getNewestPrice(symbol);

        assertEquals(newest, result);
    }

    @Test
    void testGetOldestPrice_withMultiplePrices_returnsOldest() {
        String symbol = "BTC";
        CryptoPrice oldest = new CryptoPrice();
        oldest.setTimestamp(LocalDateTime.now().minusDays(5));

        CryptoPrice anotherPrice = new CryptoPrice();
        anotherPrice.setTimestamp(LocalDateTime.now().minusDays(2));

        List<CryptoPrice> prices = List.of(oldest, anotherPrice);
        when(mockRepository.findBySymbol(symbol)).thenReturn(prices);

        CryptoPrice result = cryptoService.getOldestPrice(symbol);

        assertEquals(oldest, result);
        verify(mockRepository).findBySymbol(symbol);
    }

    @Test
    void testGetMaxPrice_noPricesInDuration_returnsZero() {
        String symbol = "BTC";
        when(mockRepository.findBySymbolAndTimestampBetween(eq(symbol), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        BigDecimal result = cryptoService.getMaxPrice(symbol, 12);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testGetMaxPrice_withPrices_returnsMax() {
        String symbol = "BTC";
        CryptoPrice price1 = new CryptoPrice();
        price1.setPrice(new BigDecimal("10000"));
        price1.setTimestamp(LocalDateTime.now().minusMonths(2));

        CryptoPrice price2 = new CryptoPrice();
        price2.setPrice(new BigDecimal("20000"));
        price2.setTimestamp(LocalDateTime.now().minusMonths(1));

        List<CryptoPrice> prices = List.of(price1, price2);
        when(mockRepository.findBySymbolAndTimestampBetween(eq(symbol), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(prices);

        BigDecimal result = cryptoService.getMaxPrice(symbol, 12);

        assertEquals(new BigDecimal("20000"), result);
    }

    @Test
    void testGetMinPrice_withPrices_returnsMin() {
        String symbol = "BTC";
        CryptoPrice price1 = new CryptoPrice();
        price1.setPrice(new BigDecimal("10000"));
        price1.setTimestamp(LocalDateTime.now().minusMonths(2));

        CryptoPrice price2 = new CryptoPrice();
        price2.setPrice(new BigDecimal("5000"));
        price2.setTimestamp(LocalDateTime.now().minusMonths(1));

        List<CryptoPrice> prices = List.of(price1, price2);
        when(mockRepository.findBySymbolAndTimestampBetween(eq(symbol), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(prices);

        BigDecimal result = cryptoService.getMinPrice(symbol, 12);

        assertEquals(new BigDecimal("5000"), result);
    }

    @Test
    void testCalculateNormalizedRange_noPrices_throwsException() {
        String symbol = "BTC";
        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();
        when(mockRepository.findBySymbolAndTimestampBetween(eq(symbol), eq(start), eq(end)))
                .thenReturn(List.of());

        assertThrows(NoContentException.class, () -> cryptoService.calculateNormalizedRange(symbol, start, end));
    }

    @Test
    void testCalculateNormalizedRange_withPrices_returnsNormalizedRange() {
        String symbol = "BTC";
        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();

        CryptoPrice price1 = new CryptoPrice();
        price1.setPrice(new BigDecimal("10000"));
        price1.setTimestamp(LocalDateTime.now().minusWeeks(3));

        CryptoPrice price2 = new CryptoPrice();
        price2.setPrice(new BigDecimal("5000"));
        price2.setTimestamp(LocalDateTime.now().minusWeeks(1));

        List<CryptoPrice> prices = List.of(price1, price2);
        when(mockRepository.findBySymbolAndTimestampBetween(eq(symbol), eq(start), eq(end)))
                .thenReturn(prices);

        BigDecimal result = cryptoService.calculateNormalizedRange(symbol, start, end);

        assertEquals(new BigDecimal("1"), result);
    }

    @Test
    void testGetCryptosSortedByNormalizedRange_withMultipleSymbols_returnsSortedList() {
        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();

        CryptoPrice btcPrice1 = new CryptoPrice();
        btcPrice1.setPrice(new BigDecimal("10000"));
        btcPrice1.setTimestamp(LocalDateTime.now().minusWeeks(3));
        btcPrice1.setSymbol("BTC");

        CryptoPrice btcPrice2 = new CryptoPrice();
        btcPrice2.setPrice(new BigDecimal("5000"));
        btcPrice2.setTimestamp(LocalDateTime.now().minusWeeks(1));
        btcPrice2.setSymbol("BTC");

        CryptoPrice ethPrice1 = new CryptoPrice();
        ethPrice1.setPrice(new BigDecimal("2000"));
        ethPrice1.setTimestamp(LocalDateTime.now().minusWeeks(2));
        ethPrice1.setSymbol("ETH");

        CryptoPrice ethPrice2 = new CryptoPrice();
        ethPrice2.setPrice(new BigDecimal("1500"));
        ethPrice2.setTimestamp(LocalDateTime.now().minusWeeks(1));
        ethPrice2.setSymbol("ETH");

        List<CryptoPrice> allPrices = List.of(btcPrice1, btcPrice2, ethPrice1, ethPrice2);

        when(mockRepository.findByTimestampBetween(eq(start), eq(end))).thenReturn(allPrices);

        List<CryptoRange> result = cryptoService.getCryptosSortedByNormalizedRange(start, end);

        assertEquals(2, result.size());

        assertTrue(result.get(0).normalizedRange().compareTo(result.get(1).normalizedRange()) > 0);
    }

    @Test
    void testGetCryptoWithHighestNormalizedRange_returnsCryptoWithHighestRange() {
        LocalDateTime day = LocalDateTime.now().minusDays(1);

        CryptoPrice btcPrice1 = new CryptoPrice();
        btcPrice1.setPrice(new BigDecimal("10000"));
        btcPrice1.setTimestamp(LocalDateTime.now().minusDays(1).minusHours(2));
        btcPrice1.setSymbol("BTC");

        CryptoPrice btcPrice2 = new CryptoPrice();
        btcPrice2.setPrice(new BigDecimal("5000"));
        btcPrice2.setTimestamp(LocalDateTime.now().minusDays(1).minusHours(1));
        btcPrice2.setSymbol("BTC");

        List<CryptoPrice> allPrices = List.of(btcPrice1, btcPrice2);

        when(mockRepository.findByTimestampBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(allPrices);

        when(mockRepository.findBySymbol(eq("BTC"))).thenReturn(List.of(btcPrice1, btcPrice2));

        CryptoPrice result = cryptoService.getCryptoWithHighestNormalizedRange(day);

        assertEquals(btcPrice1, result);

        verify(mockRepository).findByTimestampBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

}