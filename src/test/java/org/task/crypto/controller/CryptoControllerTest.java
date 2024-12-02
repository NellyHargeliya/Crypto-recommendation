package org.task.crypto.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.task.crypto.dto.CryptoRange;
import org.task.crypto.model.CryptoPrice;
import org.task.crypto.service.CryptoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {
    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private CryptoController cryptoController;

    private CryptoPrice mockCryptoPrice;

    @BeforeEach
    public void setUp() {
        mockCryptoPrice = new CryptoPrice();
        mockCryptoPrice.setSymbol("BTC");
        mockCryptoPrice.setPrice(BigDecimal.valueOf(46813.21));
        mockCryptoPrice.setTimestampFromEpochMilli(1641009600000L);

        mockCryptoPrice.setTimestamp(LocalDateTime.parse("2024-12-01T00:00:00"));
    }

    @Test
    public void testGetOldestPrice() {
        String symbol = "BTC";

        when(cryptoService.getOldestPrice(symbol)).thenReturn(mockCryptoPrice);

        CryptoPrice response = cryptoController.getOldestPrice(symbol);
        assertNotNull(response);
        assertEquals(mockCryptoPrice, response);

        verify(cryptoService, times(1)).getOldestPrice(symbol);
    }

    @Test
    public void testGetNewestPrice() {
        String symbol = "BTC";
        CryptoPrice mockNewestCryptoPrice = new CryptoPrice();
        mockNewestCryptoPrice.setSymbol("BTC");
        mockNewestCryptoPrice.setPrice(BigDecimal.valueOf(50000));
        mockNewestCryptoPrice.setTimestampFromEpochMilli(1641009600000L);

        mockCryptoPrice.setTimestamp(LocalDateTime.parse("2024-12-02T00:00:00"));

        when(cryptoService.getNewestPrice(symbol)).thenReturn(mockNewestCryptoPrice);

        CryptoPrice response = cryptoController.getNewestPrice(symbol);

        assertNotNull(response);
        assertEquals(mockNewestCryptoPrice, response);
    }

    @Test
    public void testGetPrice() {
        String symbol = "BTC";
        String type = "MIN";
        Integer months = 1;
        BigDecimal expectedPrice = new BigDecimal("45000.00");

        when(cryptoService.getMinPrice(symbol, months)).thenReturn(expectedPrice);

        BigDecimal response = cryptoController.getPrice(symbol, type, months);

        assertEquals(expectedPrice, response);
    }

    @Test
    public void testGetNormalizedRange() {
        String symbol = "BTC";
        LocalDateTime start = LocalDateTime.of(2024, 12, 1, 0, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 30, 0, 0, 0, 0);
        BigDecimal expectedRange = new BigDecimal("0.2");

        when(cryptoService.calculateNormalizedRange(symbol, start, end)).thenReturn(expectedRange);

        BigDecimal response = cryptoController.getNormalizedRange(symbol, start, end);

        assertEquals(expectedRange, response);
    }

    @Test
    public void testGetSortedCryptosByNormalizedRange() {
        LocalDateTime start = LocalDateTime.of(2024, 12, 1, 0, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 30, 0, 0, 0, 0);
        List<CryptoRange> expectedRanges = List.of(new CryptoRange("BTC", new BigDecimal("0.2")));

        when(cryptoService.getCryptosSortedByNormalizedRange(start, end)).thenReturn(expectedRanges);

        List<CryptoRange> response = cryptoController.getSortedCryptosByNormalizedRange(start, end);

        assertNotNull(response.getFirst());
        assertEquals(expectedRanges, response);
    }
}