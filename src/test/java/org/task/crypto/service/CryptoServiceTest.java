package org.task.crypto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.task.crypto.exception.NoContentException;
import org.task.crypto.model.CryptoPrice;
import org.task.crypto.repository.CryptoPriceRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(mockRepository.findBySymbol(symbol)).thenReturn(Collections.emptyList());

        List<CryptoPrice> result = cryptoService.getCryptoPrices(symbol);

        assertTrue(result.isEmpty());
        verify(mockRepository).findBySymbol(symbol);
    }

    @Test
    void testGetOldestPrice_noPrices_throwsException() {
        String symbol = "BTC";
        when(mockRepository.findBySymbol(symbol)).thenReturn(Collections.emptyList());

        assertThrows(NoContentException.class, () -> cryptoService.getOldestPrice(symbol));
        verify(mockRepository).findBySymbol(symbol);
    }

    @Test
    void testGetNewestPrice_withMultiplePrices_returnsNewest() {
        String symbol = "BTC";
        CryptoPrice newest = new CryptoPrice();
        newest.setTimestamp(LocalDateTime.now());  // Correctly set

        CryptoPrice anotherPrice = new CryptoPrice();
        anotherPrice.setTimestamp(LocalDateTime.now().minusDays(1));  // Set this too!

        List<CryptoPrice> prices = List.of(anotherPrice, newest);
        when(mockRepository.findBySymbol(symbol)).thenReturn(prices);

        CryptoPrice result = cryptoService.getNewestPrice(symbol);

        assertEquals(newest, result);
    }

    @Test
    void testGetOldestPrice_withMultiplePrices_returnsOldest() {
        String symbol = "BTC";
        CryptoPrice oldest = new CryptoPrice();
        oldest.setTimestamp(LocalDateTime.now().minusDays(5));  // Correctly set

        CryptoPrice anotherPrice = new CryptoPrice();
        anotherPrice.setTimestamp(LocalDateTime.now().minusDays(2));  // Correctly set too!

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
                .thenReturn(Collections.emptyList());

        BigDecimal result = cryptoService.getMaxPrice(symbol, 12);

        assertEquals(BigDecimal.ZERO, result);
    }

}