package org.task.crypto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.task.crypto.dto.CryptoPriceDto;
import org.task.crypto.exception.NoContentException;
import org.task.crypto.repository.CryptoPriceRepository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class CryptoPriceServiceTest {

    @Mock
    private CryptoPriceRepository cryptoPriceRepository;

    @InjectMocks
    private CryptoPriceService cryptoPriceService;

    private MockMultipartFile correctFile;
    private MockMultipartFile incorrectFile;
    private MockMultipartFile emptyFile;

    @BeforeEach
    public void setUp() {
        String correctCsvContent = "timestamp,symbol,price\n1638326400000,BTC,59000.00\n1638326400001,ETH,4500.00";
        correctFile = new MockMultipartFile("data", "filename.csv", "text/csv", correctCsvContent.getBytes(StandardCharsets.UTF_8));

        String incorrectCsvContent = "timestamp,symbol,price\nbadtimestamp,BTC,not_a_price";
        incorrectFile = new MockMultipartFile("data", "bad.csv", "text/csv", incorrectCsvContent.getBytes(StandardCharsets.UTF_8));

        String emptyCsvContent = "";
        emptyFile = new MockMultipartFile("data", "empty.csv", "text/csv", emptyCsvContent.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testLoadCryptoPrices_withCorrectFile_shouldReturnPrices() throws Exception {
        List<CryptoPriceDto> prices = cryptoPriceService.loadCryptoPrices(correctFile);

        assertNotNull(prices);
        assertEquals(2, prices.size());
        assertEquals(new BigDecimal("59000.00"), prices.get(0).price());
        assertEquals("BTC", prices.get(0).symbol());
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
    public void testLoadAllCsvFiles_withNoFiles_shouldThrowNoContentException() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(any(Path.class))).thenReturn(Stream.empty());

            Exception exception = assertThrows(NoContentException.class, () -> {
                cryptoPriceService.loadAllCsvFiles();
            });

            assertEquals("No content available in the CSV files.", exception.getMessage());
        }
    }
}