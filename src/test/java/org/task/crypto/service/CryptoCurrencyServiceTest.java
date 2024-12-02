package org.task.crypto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.task.crypto.model.CryptoCurrency;
import org.task.crypto.repository.CryptoCurrencyRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CryptoCurrencyServiceTest {

    @Mock
    private CryptoCurrencyRepository cryptoCurrencyRepository;

    @InjectMocks
    private CryptoCurrencyService cryptoCurrencyService;

    private CryptoCurrency btc;

    @BeforeEach
    public void setUp() {
        btc = new CryptoCurrency();
        btc.setId(1L);
        btc.setSymbol("BTC");
        btc.setName("Bitcoin");
    }

    @Test
    public void testAddCryptoCurrency_NewSymbol_Success() {
        when(cryptoCurrencyRepository.findBySymbol("BTC")).thenReturn(Optional.empty());
        when(cryptoCurrencyRepository.save(any(CryptoCurrency.class))).thenReturn(btc);

        CryptoCurrency created = cryptoCurrencyService.addCryptoCurrency(btc);

        assertNotNull(created);
        assertEquals(btc.getSymbol(), created.getSymbol());
        assertEquals(btc.getName(), created.getName());
        verify(cryptoCurrencyRepository).save(btc);
    }

    @Test
    public void testAddCryptoCurrency_ExistingSymbol_ThrowsException() {
        when(cryptoCurrencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(btc));

        assertThrows(IllegalArgumentException.class, () -> cryptoCurrencyService.addCryptoCurrency(btc));
    }

    @Test
    public void testRemoveCryptoCurrency_ExistingSymbol_Success() {
        when(cryptoCurrencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(btc));

        assertDoesNotThrow(() -> cryptoCurrencyService.removeCryptoCurrency("BTC"));
        verify(cryptoCurrencyRepository).delete(btc);
    }

    @Test
    public void testRemoveCryptoCurrency_NonExistingSymbol_ThrowsException() {
        when(cryptoCurrencyRepository.findBySymbol("BTC")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cryptoCurrencyService.removeCryptoCurrency("BTC"));
    }

    @Test
    public void testGetAllCryptoCurrencies() {
        List<CryptoCurrency> cryptoCurrencies = List.of(btc);
        when(cryptoCurrencyRepository.findAll()).thenReturn(cryptoCurrencies);

        List<CryptoCurrency> result = cryptoCurrencyService.getAllCryptoCurrencies();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(btc, result.getFirst());
    }

}