package org.task.crypto.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.task.crypto.dto.CryptoPriceDto;
import org.task.crypto.exception.NoContentException;
import org.task.crypto.model.CryptoPrice;
import org.task.crypto.repository.CryptoPriceRepository;
import org.task.crypto.utils.CustomMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoPriceService {
    private final CryptoPriceRepository cryptoPriceRepository;

    @Value("${crypto.prices-directory}")
    private String pricesDirectory;

    public List<CryptoPriceDto> loadCryptoPrices(MultipartFile file) {
        List<CryptoPriceDto> prices = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.Builder.create(CSVFormat.DEFAULT)
                             .setHeader()
                             .setIgnoreHeaderCase(true)
                             .setTrim(true)
                             .build())) {

            for (CSVRecord csvRecord : csvParser) {
                try {
                    long timestamp = Long.parseLong(csvRecord.get("timestamp"));
                    String symbol = csvRecord.get("symbol");
                    BigDecimal price = new BigDecimal(csvRecord.get("price"));

                    CryptoPriceDto cryptoPrice = new CryptoPriceDto(timestamp, symbol, price);
                    prices.add(cryptoPrice);
                } catch (NumberFormatException e) {
                    log.warn("Invalid data format in record: {}", csvRecord, e);
                }
            }

        } catch (IOException e) {
            log.error("Error reading the file: {}", file.getOriginalFilename(), e);
        }
        return prices;
    }

    public void loadAllCsvFiles() {
        List<CryptoPriceDto> allPrices = new ArrayList<>();
        try {
            List<File> filesInFolder = Files.walk(Paths.get(pricesDirectory))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .map(Path::toFile)
                    .toList();
            if (filesInFolder.isEmpty()) {
                log.warn("No CSV files found in directory {}", pricesDirectory);
            }
            for (File file : filesInFolder) {
                try {
                    String contentType = Files.probeContentType(file.toPath());
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    MultipartFile multipartFile = new CustomMultipartFile(
                            file.getName(),
                            contentType,
                            fileContent
                    );

                    List<CryptoPriceDto> prices = loadCryptoPrices(multipartFile);
                    allPrices.addAll(prices);
                    saveCryptoPricesToDatabase(prices);
                } catch (IOException e) {
                    log.error("Error reading file {}: {}", file.getName(), e.getMessage(), e);
                }
            }

            if (allPrices.isEmpty()) {
                throw new NoContentException("No content available in the CSV files.");
            }

        } catch (IOException e) {
            log.error("Error walking through directory {}: {}", pricesDirectory, e.getMessage(), e);
        }
    }

    private void saveCryptoPricesToDatabase(List<CryptoPriceDto> prices) {
        List<CryptoPrice> cryptoPricesToSave = prices.stream()
                .map(this::convertToCryptoPriceEntity)
                .toList();

        cryptoPriceRepository.saveAll(cryptoPricesToSave);
    }

    private CryptoPrice convertToCryptoPriceEntity(CryptoPriceDto dto) {
        CryptoPrice cryptoPrice = new CryptoPrice();
        cryptoPrice.setSymbol(dto.symbol());
        cryptoPrice.setPrice(dto.price());
        cryptoPrice.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(dto.timestamp()), ZoneId.systemDefault()));
        return cryptoPrice;
    }

}
