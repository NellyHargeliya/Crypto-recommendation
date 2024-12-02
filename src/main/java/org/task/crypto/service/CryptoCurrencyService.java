package org.task.crypto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.task.crypto.model.CryptoCurrency;
import org.task.crypto.repository.CryptoCurrencyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoCurrencyService {

    private final CryptoCurrencyRepository cryptoCurrencyRepository;

    public CryptoCurrency addCryptoCurrency(CryptoCurrency cryptoCurrency) {
        cryptoCurrencyRepository.findBySymbol(cryptoCurrency.getSymbol())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Cryptocurrency with this symbol already exists.");
                });
        return cryptoCurrencyRepository.save(cryptoCurrency);
    }

    public void removeCryptoCurrency(String symbol) {
        CryptoCurrency cryptoCurrency = cryptoCurrencyRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Cryptocurrency with this symbol does not exist."));
        cryptoCurrencyRepository.delete(cryptoCurrency);
    }

    public List<CryptoCurrency> getAllCryptoCurrencies() {
        return cryptoCurrencyRepository.findAll();
    }
}
