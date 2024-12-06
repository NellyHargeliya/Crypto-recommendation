package org.task.crypto.unit.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.task.crypto.model.CryptoCurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("unit")
public class CryptoCurrencyTest {

    @Test
    void testCryptoCurrencyEntity() {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setSymbol("BTC");
        cryptoCurrency.setName("Bitcoin");

        String symbol = cryptoCurrency.getSymbol();
        String name = cryptoCurrency.getName();

        assertNotNull(cryptoCurrency);
        assertEquals("BTC", symbol);
        assertEquals("Bitcoin", name);
    }

    @Test
    void testCryptoCurrencyId() {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setId(1L);

        Long id = cryptoCurrency.getId();
        assertNotNull(cryptoCurrency);
        assertEquals(1L, id);
    }
}
