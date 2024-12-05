package org.task.crypto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.task.crypto.dto.CryptoRange;
import org.task.crypto.enums.PriceType;
import org.task.crypto.model.CryptoPrice;
import org.task.crypto.service.CryptoService;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/crypto")
public class CryptoController {

    private final CryptoService service;

    public CryptoController(CryptoService service) {
        this.service = service;
    }

    @Operation(summary = "Get the oldest price for a specific cryptocurrency")
    @ApiResponse(responseCode = "200", description = "Successfully fetched the oldest price")
    @GetMapping("/{symbol}/oldest")
    @ResponseStatus(HttpStatus.OK)
    public CryptoPrice getOldestPrice(@PathVariable String symbol) {
        return service.getOldestPrice(symbol);
    }

    @Operation(summary = "Get the newest price for a specific cryptocurrency")
    @ApiResponse(responseCode = "200", description = "Successfully fetched the newest price")
    @GetMapping("/{symbol}/newest")
    @ResponseStatus(HttpStatus.OK)
    public CryptoPrice getNewestPrice(@PathVariable String symbol) {
        return service.getNewestPrice(symbol);
    }

    @Operation(summary = "Get the min/max price for a cryptocurrency within a given time frame")
    @ApiResponse(responseCode = "200", description = "Successfully fetched the min/max price")
    @GetMapping("/{symbol}/price/{type}")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getPrice(@PathVariable String symbol,
                               @PathVariable("type") String typeStr,
                               @RequestParam(defaultValue = "1") @Min(1) Integer months) {
        PriceType type = PriceType.fromString(typeStr);
        return switch (type) {
            case MIN -> service.getMinPrice(symbol, months);
            case MAX -> service.getMaxPrice(symbol, months);
        };
    }

    @Operation(
            summary = "Get the normalized price range for a given cryptocurrency within a time frame",
            description = "Calculates the normalized range (i.e., (max - min) / min) for the provided cryptocurrency between the start and end date."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the normalized range")
    @GetMapping("/{symbol}/normalized-range")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getNormalizedRange(
            @Parameter(description = "The symbol of the cryptocurrency")
            @PathVariable String symbol,
            @Parameter(description = "The start date for the time frame")
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "The end date for the time frame")
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return service.calculateNormalizedRange(symbol, start, end);
    }

    @Operation(
            summary = "Get a list of cryptocurrencies sorted by their normalized range",
            description = "Returns a list of all cryptocurrencies sorted by their normalized range (i.e., (max - min) / min) within a specific time frame."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of cryptocurrencies sorted by normalized range")
    @GetMapping("/sorted-normalized-range")
    @ResponseStatus(HttpStatus.OK)
    public List<CryptoRange> getSortedCryptosByNormalizedRange(
            @Parameter(description = "The start date for the time frame")
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "The end date for the time frame") @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return service.getCryptosSortedByNormalizedRange(start, end);
    }

    @Operation(
            summary = "Get the cryptocurrency with the highest normalized range for a specific day",
            description = "Returns the cryptocurrency with the highest normalized range for the given date (i.e., (max - min) / min)."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the crypto with the highest normalized range")
    @GetMapping("/highest-normalized-range")
    @ResponseStatus(HttpStatus.OK)
    public CryptoPrice getCryptoWithHighestNormalizedRange(
            @Parameter(description = "The date for which to retrieve the cryptocurrency with the highest normalized range")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getCryptoWithHighestNormalizedRange(date.atStartOfDay());
    }
}
