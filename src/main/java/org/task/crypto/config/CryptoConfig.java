package org.task.crypto.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.task.crypto.model.CryptoCurrency;
import org.task.crypto.repository.CryptoCurrencyRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CryptoConfig {
    private final CryptoCurrencyRepository cryptoCurrencyRepository;

    @Bean
    public List<String> allowedCryptocurrencies() {
        return cryptoCurrencyRepository.findAll().stream()
                .map(CryptoCurrency::getSymbol)
                .toList();
    }
}
