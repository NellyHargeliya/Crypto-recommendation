package org.task.crypto.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.task.crypto.model.CryptoCurrency;
import org.task.crypto.model.CryptoPrice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CryptoPriceRepositoryTest {

    @Autowired
    private CryptoPriceRepository cryptoPriceRepository;

    @Autowired
    private CryptoCurrencyRepository cryptoCurrencyRepository;

    private CryptoCurrency bitcoin;
    private CryptoPrice bitcoinPrice1;
    private CryptoPrice bitcoinPrice2;
    private CryptoPrice bitcoinPrice3;

    @BeforeEach
    public void setUp() {
        bitcoin = new CryptoCurrency();
        bitcoin.setSymbol("BTC");
        bitcoin.setName("Bitcoin");
        cryptoCurrencyRepository.save(bitcoin);

        bitcoinPrice1 = new CryptoPrice();
        bitcoinPrice1.setSymbol("BTC");
        bitcoinPrice1.setPrice(BigDecimal.valueOf(50000.0));
        bitcoinPrice1.setTimestampFromEpochMilli(1704067199000L);
        bitcoinPrice1.setCryptoCurrency(bitcoin);


        bitcoinPrice2 = new CryptoPrice();
        bitcoinPrice2.setSymbol("BTC");
        bitcoinPrice2.setPrice(BigDecimal.valueOf(52000.0));
        bitcoinPrice2.setTimestampFromEpochMilli(1704153600000L);
        bitcoinPrice2.setCryptoCurrency(bitcoin);

        cryptoPriceRepository.save(bitcoinPrice1);
        cryptoPriceRepository.save(bitcoinPrice2);

    }

    @Test
    public void testFindBySymbol() {
        List<CryptoPrice> prices = cryptoPriceRepository.findBySymbol("BTC");
        assertThat(prices).hasSize(2);
        assertThat(prices).extracting(CryptoPrice::getSymbol).containsOnly("BTC");
    }

    @Test
    public void testFindBySymbolAndTimestampBetween() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 23, 59, 59);

        List<CryptoPrice> prices = cryptoPriceRepository.findBySymbolAndTimestampBetween("BTC", start, end);
        assertThat(prices).hasSize(1);
        assertThat(prices.getFirst().getSymbol()).isEqualTo("BTC");
        assertThat(prices.getFirst().getTimestamp()).isEqualTo(bitcoinPrice1.getTimestamp());
    }

    @Test
    public void testFindByTimestampBetween() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 3, 0, 0, 0);
        bitcoinPrice3 = new CryptoPrice();
        bitcoinPrice3.setSymbol("BTC");
        bitcoinPrice3.setPrice(BigDecimal.valueOf(55000.0));
        bitcoinPrice3.setTimestampFromEpochMilli(1704067199000L);
        bitcoinPrice3.setCryptoCurrency(bitcoin);
        cryptoPriceRepository.save(bitcoinPrice3);

        List<CryptoPrice> prices = cryptoPriceRepository.findByTimestampBetween(start, end);
        assertThat(prices).hasSize(3);
        assertThat(prices).extracting(CryptoPrice::getTimestamp)
                .allMatch(timestamp -> !timestamp.isBefore(start) && !timestamp.isAfter(end));
    }
}