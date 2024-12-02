package org.task.crypto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.task.crypto.service.CryptoPriceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/csv")
public class CSVController {

    private final CryptoPriceService cryptoPriceService;

    @Operation(
            summary = "Upload a single CSV file",
            description = "Uploads a single CSV file containing cryptocurrency prices and processes it."
    )
    @ApiResponse(responseCode = "200", description = "File successfully uploaded and processed")
    @ApiResponse(responseCode = "400", description = "Invalid file format")
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(
            @Parameter(description = "The CSV file containing cryptocurrency prices to be uploaded")
            @RequestParam("file") MultipartFile file) {
        cryptoPriceService.loadCryptoPrices(file);
    }

    @Operation(
            summary = "Upload all CSV files",
            description = "Uploads and processes all available CSV files containing cryptocurrency prices."
    )
    @ApiResponse(responseCode = "200", description = "All files successfully uploaded and processed")
    @GetMapping("/upload/all")
    @ResponseStatus(HttpStatus.OK)
    public void uploadAllFiles() {
        cryptoPriceService.loadAllCsvFiles();
    }
}