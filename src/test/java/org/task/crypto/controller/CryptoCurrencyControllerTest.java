package org.task.crypto.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.task.crypto.model.CryptoCurrency;
import org.task.crypto.service.CryptoCurrencyService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoCurrencyControllerTest {

    @Mock
    private CryptoCurrencyService cryptoCurrencyService;

    @InjectMocks
    private CryptoCurrencyController cryptoCurrencyController;

    @Test
    void testAddCryptoCurrency() {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setSymbol("BTC");
        cryptoCurrency.setName("Bitcoin");

        CryptoCurrency savedCrypto = new CryptoCurrency();
        savedCrypto.setId(1L);
        savedCrypto.setSymbol("BTC");
        savedCrypto.setName("Bitcoin");

        when(cryptoCurrencyService.addCryptoCurrency(any(CryptoCurrency.class))).thenReturn(savedCrypto);

        ResponseEntity<CryptoCurrency> response = cryptoCurrencyController.addCryptoCurrency(cryptoCurrency);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BTC", response.getBody().getSymbol());
        verify(cryptoCurrencyService, times(1)).addCryptoCurrency(any(CryptoCurrency.class));
    }

    @Test
    void testRemoveCryptoCurrency() {
        String symbol = "BTC";

        doNothing().when(cryptoCurrencyService).removeCryptoCurrency(symbol);

        ResponseEntity<Void> response = cryptoCurrencyController.removeCryptoCurrency(symbol);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cryptoCurrencyService, times(1)).removeCryptoCurrency(symbol);
    }

    @Test
    void testGetAllCryptoCurrencies() {
        CryptoCurrency cryptoCurrency1 = new CryptoCurrency();
        cryptoCurrency1.setId(1L);
        cryptoCurrency1.setSymbol("BTC");
        cryptoCurrency1.setName("Bitcoin");

        CryptoCurrency cryptoCurrency2 = new CryptoCurrency();
        cryptoCurrency2.setId(2L);
        cryptoCurrency2.setSymbol("ETH");
        cryptoCurrency2.setName("Ethereum");

        List<CryptoCurrency> cryptoCurrencies = List.of(cryptoCurrency1, cryptoCurrency2);

        when(cryptoCurrencyService.getAllCryptoCurrencies()).thenReturn(cryptoCurrencies);

        List<CryptoCurrency> response = cryptoCurrencyController.getAllCryptoCurrencies();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("BTC", response.get(0).getSymbol());
        assertEquals("ETH", response.get(1).getSymbol());
        verify(cryptoCurrencyService, times(1)).getAllCryptoCurrencies();
    }
}
