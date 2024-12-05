package org.task.crypto.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.task.crypto.dto.CryptoPriceDto;
import org.task.crypto.exception.GlobalExceptionHandler;
import org.task.crypto.repository.CryptoPriceRepository;
import org.task.crypto.service.CryptoPriceService;
import org.task.crypto.validation.FileTypeValidator;
import org.task.crypto.validation.ValidFileType;

import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CSVControllerTest {
    @Mock
    private MockMvc mockMvc;

    @Mock
    private CryptoPriceService cryptoPriceService;

    @InjectMocks
    private CSVController csvController;

    @Mock
    private List<String> allowedCryptocurrencies;
    @Mock
    CryptoPriceRepository cryptoPriceRepository;
    @Mock
    private FileTypeValidator fileTypeValidator;
    @Mock
    private ValidFileType validFileType;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(csvController)
                .setControllerAdvice(new GlobalExceptionHandler(allowedCryptocurrencies))
                .build();
    }

    @Test
    void shouldRejectUploadIfFileIsInvalidType() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "".getBytes()
        );
        mockMvc.perform(multipart("/api/csv/upload")
                        .file(invalidFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void uploadAllFiles_ShouldReturnOk_WhenFilesAreProcessed() throws Exception {
        mockMvc.perform(get("/api/csv/upload/all"))
                .andExpect(status().isOk());

    }

    @Test
    void uploadFile_ShouldReturnOk_WhenFileContainsInvalidRecords() throws Exception {
        MockMultipartFile fileWithInvalidRecords = new MockMultipartFile(
                "file",
                "partial.csv",
                "text/csv",
                "timestamp,symbol,price\n1641009600000,BTC,46813.21\ninvalid,ETH,4000.50".getBytes()
        );

        List<CryptoPriceDto> partialResult = List.of(
                new CryptoPriceDto(1641009600000L, "BTC", new BigDecimal("46813.21"))
        );
        when(cryptoPriceService.loadCryptoPrices(fileWithInvalidRecords)).thenReturn(partialResult);

        mockMvc.perform(multipart("/api/csv/upload").file(fileWithInvalidRecords))
                .andExpect(status().isOk());

        verify(cryptoPriceService, times(1)).loadCryptoPrices(fileWithInvalidRecords);
    }

    @Test
    void uploadFile_ShouldReturnOk_WhenFileIsValid() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv",
                "timestamp,symbol,price\n1641009600000,BTC,46813.21".getBytes()
        );
        mockMvc.perform(multipart("/api/csv/upload")
                        .file(file))
                .andExpect(status().isOk());
    }

}
