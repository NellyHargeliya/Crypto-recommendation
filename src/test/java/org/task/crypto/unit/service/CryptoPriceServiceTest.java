package org.task.crypto.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.task.crypto.dto.CryptoPriceDto;
import org.task.crypto.exception.NoContentException;
import org.task.crypto.model.CryptoPrice;
import org.task.crypto.repository.CryptoPriceRepository;
import org.task.crypto.service.CryptoPriceService;
import org.task.crypto.utils.CustomMultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class CryptoPriceServiceTest {

    @Mock
    private CryptoPriceRepository cryptoPriceRepository;
    @Mock
    private Logger logger;

    private static final String PRICES_DIRECTORY = "src/test/resources/prices";
    private static final String INVALID_PRICES_DIRECTORY = "src/test/resources/invalidFiles";

    @InjectMocks
    private CryptoPriceService cryptoPriceService;

    private CustomMultipartFile correctFile;
    private CustomMultipartFile incorrectFile;
    private CustomMultipartFile emptyFile;
    private List<CryptoPriceDto> mockPrices;

    @BeforeEach
    public void setUp() throws IOException {

        ReflectionTestUtils.setField(cryptoPriceService, "pricesDirectory", PRICES_DIRECTORY);

        String correctCsvContent = "timestamp,symbol,price\n1638326400000,BTC,59000.00\n1638326400001,ETH,4500.00";
        correctFile = new CustomMultipartFile("filename.csv", "text/csv", correctCsvContent.getBytes(StandardCharsets.UTF_8));

        String incorrectCsvContent = "timestamp,symbol,price\nbadtimestamp,BTC,not_a_price";
        incorrectFile = new CustomMultipartFile("bad.csv", "text/csv", incorrectCsvContent.getBytes(StandardCharsets.UTF_8));

        String emptyCsvContent = "";
        emptyFile = new CustomMultipartFile("empty.csv", "text/csv", emptyCsvContent.getBytes(StandardCharsets.UTF_8));
        mockPrices = new ArrayList<>();
        mockPrices.add(new CryptoPriceDto(1638326400000L, "BTC", new BigDecimal("59000.00")));
        mockPrices.add(new CryptoPriceDto(1638326400001L, "ETH", new BigDecimal("4500.00")));

    }

    @Test
    public void testLoadCryptoPrices_withCorrectFile_shouldReturnPrices() throws Exception {
        List<CryptoPriceDto> prices = cryptoPriceService.loadCryptoPrices(correctFile);

        assertNotNull(prices);
        assertEquals(2, prices.size());
        assertEquals(new BigDecimal("59000.00"), prices.getFirst().price());
        assertEquals("BTC", prices.getFirst().symbol());
    }

    @Test
    public void testLoadCryptoPrices_withIncorrectFile_shouldLogWarnings() throws Exception {
        List<CryptoPriceDto> prices = cryptoPriceService.loadCryptoPrices(incorrectFile);

        assertTrue(prices.isEmpty());
    }

    @Test
    public void testLoadCryptoPrices_withEmptyFile_shouldReturnEmptyList() throws Exception {
        List<CryptoPriceDto> prices = cryptoPriceService.loadCryptoPrices(emptyFile);

        assertTrue(prices.isEmpty());
    }

    @Test
    public void testSaveCryptoPricesToDatabase_shouldSavePrices() throws Exception {
        Method saveMethod = CryptoPriceService.class.getDeclaredMethod("saveCryptoPricesToDatabase", List.class);
        saveMethod.setAccessible(true);

        saveMethod.invoke(cryptoPriceService, mockPrices);

        verify(cryptoPriceRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testConvertToCryptoPriceEntity_shouldConvertDtoToEntity() throws Exception {
        Method convertMethod = CryptoPriceService.class.getDeclaredMethod("convertToCryptoPriceEntity", CryptoPriceDto.class);
        convertMethod.setAccessible(true);

        CryptoPriceDto dto = mockPrices.getFirst();

        CryptoPrice cryptoPrice = (CryptoPrice) convertMethod.invoke(cryptoPriceService, dto);

        assertNotNull(cryptoPrice);
        assertEquals(dto.symbol(), cryptoPrice.getSymbol());
        assertEquals(dto.price(), cryptoPrice.getPrice());
        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(dto.timestamp()), ZoneId.systemDefault()), cryptoPrice.getTimestamp());
    }

    @Test
    public void testLoadCryptoPrices_withFileHavingExtraColumns_shouldIgnoreExtraColumns() throws Exception {
        String csvContentWithExtraColumns = "timestamp,symbol,price,extraColumn\n1638326400000,BTC,59000.00,someData\n1638326400001,ETH,4500.00,moreData";
        MockMultipartFile fileWithExtraColumns = new MockMultipartFile(
                "data", "extra_columns.csv", "text/csv", csvContentWithExtraColumns.getBytes(StandardCharsets.UTF_8));

        List<CryptoPriceDto> prices = cryptoPriceService.loadCryptoPrices(fileWithExtraColumns);

        assertNotNull(prices);
        assertEquals(2, prices.size());
        assertEquals("BTC", prices.getFirst().symbol());
        assertEquals(new BigDecimal("59000.00"), prices.getFirst().price());
    }

    @Test
    public void testLoadCryptoPrices_withMissingColumns_shouldLogErrorAndReturnEmptyList() throws Exception {
        String csvContentWithMissingColumns = "symbol,price\nBTC,59000.00\nETH,4500.00";
        MockMultipartFile fileWithMissingColumns = new MockMultipartFile(
                "data", "missing_columns.csv", "text/csv", csvContentWithMissingColumns.getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cryptoPriceService.loadCryptoPrices(fileWithMissingColumns);
        });

        assertEquals(exception.getMessage(), "Mapping for timestamp not found, expected one of [price, symbol]");
    }

    @Test
    public void testConvertToCryptoPriceEntity_withNullDto_shouldThrowException() throws Exception {
        Method convertMethod = CryptoPriceService.class.getDeclaredMethod("convertToCryptoPriceEntity", CryptoPriceDto.class);
        convertMethod.setAccessible(true);

        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            convertMethod.invoke(cryptoPriceService, (CryptoPriceDto) null);
        });

        assertEquals("Cannot invoke \"org.task.crypto.dto.CryptoPriceDto.symbol()\" because \"dto\" is null", exception.getCause().getMessage());
    }

    @Test
    public void testLoadCryptoPrices_withMalformedCsv_shouldLogErrorAndReturnEmptyList() throws Exception {
        String malformedCsvContent = "timestamp,symbol,price\n1638326400000,BTC,59000.00\nmissing_comma_ETH_4500.00";
        MockMultipartFile malformedFile = new MockMultipartFile(
                "data", "malformed.csv", "text/csv", malformedCsvContent.getBytes(StandardCharsets.UTF_8));

        List<CryptoPriceDto> prices = cryptoPriceService.loadCryptoPrices(malformedFile);

        assertNotNull(prices);
        assertEquals(1, prices.size());
        assertEquals(new BigDecimal("59000.00"), prices.getFirst().price());
        assertEquals("BTC", prices.getFirst().symbol());
    }

    @Test
    void testLoadAllCsvFiles_saveCryptoPricesToDatabase_shouldSavePrices() throws Exception {
        cryptoPriceService.loadAllCsvFiles();
        verify(cryptoPriceRepository, times(2)).saveAll(anyList());
    }

    @Test
    void testLoadAllCsvFiles_withInvalidFiles() throws Exception {
        ReflectionTestUtils.setField(cryptoPriceService, "pricesDirectory", INVALID_PRICES_DIRECTORY);
        Exception exception = assertThrows(NoContentException.class, () -> {
            cryptoPriceService.loadAllCsvFiles();
        });
        assertNotNull(exception.getMessage());
        assertEquals("No content available in the CSV files.", exception.getMessage());
    }

    @Test
    void testLoadAllCsvFiles_withInvalidFilesWithEmptyValues() throws Exception {
        ReflectionTestUtils.setField(cryptoPriceService, "pricesDirectory", INVALID_PRICES_DIRECTORY + "/empty");
        Exception exception = assertThrows(NoContentException.class, () -> {
            cryptoPriceService.loadAllCsvFiles();
        });
        assertNotNull(exception.getMessage());
        assertEquals("No content available in the CSV files.", exception.getMessage());
    }
}
