package org.task.crypto.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.task.crypto.model.CryptoCurrency;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CryptoCurrencyRepositoryTest {

    @Autowired
    private CryptoCurrencyRepository cryptoCurrencyRepository;

    @Test
    void testFindBySymbol() {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setSymbol("BTC");
        cryptoCurrency.setName("Bitcoin");
        cryptoCurrencyRepository.save(cryptoCurrency);

        Optional<CryptoCurrency> foundCryptoCurrency = cryptoCurrencyRepository.findBySymbol("BTC");

        assertTrue(foundCryptoCurrency.isPresent());
        assertEquals("BTC", foundCryptoCurrency.get().getSymbol());
        assertEquals("Bitcoin", foundCryptoCurrency.get().getName());
    }

    @Test
    void testFindBySymbolNotFound() {
        Optional<CryptoCurrency> foundCryptoCurrency = cryptoCurrencyRepository.findBySymbol("ETH");

        assertFalse(foundCryptoCurrency.isPresent());
    }

    @Test
    void testSaveAndFindById() {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setSymbol("LTC");
        cryptoCurrency.setName("Litecoin");

        CryptoCurrency savedCryptoCurrency = cryptoCurrencyRepository.save(cryptoCurrency);
        CryptoCurrency foundCryptoCurrency = cryptoCurrencyRepository.findById(savedCryptoCurrency.getId()).orElse(null);

        assertNotNull(savedCryptoCurrency);
        assertNotNull(foundCryptoCurrency);
        assertEquals("LTC", foundCryptoCurrency.getSymbol());
        assertEquals("Litecoin", foundCryptoCurrency.getName());
    }

    @Test
    void testDeleteById() {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setSymbol("DOGE");
        cryptoCurrency.setName("Dogecoin");

        CryptoCurrency savedCryptoCurrency = cryptoCurrencyRepository.save(cryptoCurrency);
        cryptoCurrencyRepository.deleteById(savedCryptoCurrency.getId());
        Optional<CryptoCurrency> foundCryptoCurrency = cryptoCurrencyRepository.findById(savedCryptoCurrency.getId());

        assertFalse(foundCryptoCurrency.isPresent());
    }
}
