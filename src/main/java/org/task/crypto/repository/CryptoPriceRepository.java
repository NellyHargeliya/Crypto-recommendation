package org.task.crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.task.crypto.model.CryptoPrice;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {

    List<CryptoPrice> findBySymbol(String symbol);

    List<CryptoPrice> findBySymbolAndTimestampBetween(String symbol, LocalDateTime start, LocalDateTime end);

    List<CryptoPrice> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

}
