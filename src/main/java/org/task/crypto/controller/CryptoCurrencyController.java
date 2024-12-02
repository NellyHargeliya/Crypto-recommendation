package org.task.crypto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.task.crypto.model.CryptoCurrency;
import org.task.crypto.service.CryptoCurrencyService;

import java.util.List;

@RestController
@RequestMapping("/api/cryptocurrencies")
public class CryptoCurrencyController {
    private final CryptoCurrencyService cryptoCurrencyService;

    public CryptoCurrencyController(CryptoCurrencyService cryptoCurrencyService) {
        this.cryptoCurrencyService = cryptoCurrencyService;
    }

    @Operation(
            summary = "Add a new cryptocurrency",
            description = "Adds a new cryptocurrency to the system with the provided data."
    )
    @ApiResponse(responseCode = "201", description = "Successfully created the cryptocurrency")
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    @PostMapping
    public ResponseEntity<CryptoCurrency> addCryptoCurrency(
            @Parameter(description = "The cryptocurrency data to be added") @RequestBody CryptoCurrency cryptoCurrency) {
        CryptoCurrency savedCrypto = cryptoCurrencyService.addCryptoCurrency(cryptoCurrency);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCrypto);
    }

    @Operation(
            summary = "Remove a cryptocurrency by its symbol",
            description = "Removes a cryptocurrency from the system using the provided symbol."
    )
    @ApiResponse(responseCode = "204", description = "Successfully deleted the cryptocurrency")
    @ApiResponse(responseCode = "404", description = "Cryptocurrency not found")
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> removeCryptoCurrency(
            @Parameter(description = "The symbol of the cryptocurrency to be removed") @PathVariable String symbol) {
        cryptoCurrencyService.removeCryptoCurrency(symbol);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get a list of all cryptocurrencies",
            description = "Returns a list of all cryptocurrencies in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of cryptocurrencies"
    )
    @GetMapping
    public List<CryptoCurrency> getAllCryptoCurrencies() {
        return cryptoCurrencyService.getAllCryptoCurrencies();
    }
}
