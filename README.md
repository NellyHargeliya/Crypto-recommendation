___

Crypto Investment Recommendation Service
==========

# Overview

This project is a recommendation service designed to help investors choose cryptocurrencies by providing key statistics based on historical prices. It reads CSV files containing cryptocurrency prices, performs calculations, and exposes various endpoints for retrieving data.

## Features

- **Upload and process cryptocurrency price data**: Allows users to upload CSV files containing price data for various cryptocurrencies.
- **Calculate and retrieve various crypto price metrics**:
    - Get the oldest/newest/min/max prices for a given cryptocurrency.
    - Get the normalized range of prices for a cryptocurrency over a specified period.
    - Retrieve a sorted list of cryptocurrencies based on their normalized range.
    - Find the cryptocurrency with the highest normalized range for a specific day.
- **Manage cryptocurrencies**: Add new cryptocurrencies to the system and remove existing ones.
- **Interactive API Documentation**: The project includes Swagger UI for interactive API documentation.

## Application Architecture

The project is built using **Spring Boot** for the backend, **PostgreSQL** for the database, and **Redis** for caching. The main features are exposed via RESTful endpoints that are documented using OpenAPI/Swagger.

- **Controllers**:
    - `/api/v1/crypto/{symbol}` - Get price information (oldest, newest, min, max, etc.) for a specific cryptocurrency.
    - `/api/cryptocurrencies` - Add, remove, and retrieve all cryptocurrencies.
    - `/api/csv` - Upload CSV files containing price data for cryptocurrencies.
- **Database**: PostgreSQL is used to store cryptocurrency data.
- **Caching**: Redis is used to cache frequently requested data to improve performance.
- **Rate Limiting**: The application is protected against excessive requests from malicious users by limiting the number of requests per IP.

### Endpoints

#### Cryptocurrency Controller (`/api/cryptocurrencies`)
- `POST /api/cryptocurrencies` - Add a new cryptocurrency.
- `DELETE /api/cryptocurrencies/{symbol}` - Remove a cryptocurrency.
- `GET /api/cryptocurrencies` - Get a list of all cryptocurrencies.

#### Crypto Controller (`/api/v1/crypto/{symbol}`)
- `GET /api/v1/crypto/{symbol}/oldest` - Get the oldest price for a cryptocurrency.
- `GET /api/v1/crypto/{symbol}/newest` - Get the newest price for a cryptocurrency.
- `GET /api/v1/crypto/{symbol}/price/{type}` - Get the min/max price for a cryptocurrency for a given time period.
- `GET /api/v1/crypto/{symbol}/normalized-range` - Get the normalized price range for a cryptocurrency over a specified time frame.
- `GET /api/v1/crypto/{symbol}/sorted-normalized-range` - Get a sorted list of cryptocurrencies by their normalized range.
- `GET /api/v1/crypto/{symbol}/highest-normalized-range` - Get the cryptocurrency with the highest normalized range for a specific day.

#### CSV Controller (`/api/csv`)
- `POST /api/csv/upload` - Upload a single CSV file with cryptocurrency price data.
- `GET /api/csv/upload/all` - Upload and process all available CSV files.

## Prerequisites

* **Java 21**
* **Spring Boot 3**
* **Gradle 9.0**
* **PostgreSQL** for database storage
* **Redis** for caching
* **Docker** for containerization
* **CSV** for importing price data
* **Swagger** for API documentation

### Environment variables & JVM properties

| Name                   | Type         | Default |
|------------------------|--------------|---------|
| spring.profiles.active | JVM property | dev     |

## Build the application

Using Gradle to build the application:

```shell
./gradlew build
```

<details><summary>Examples (click to expand)</summary>

to run app from default root folder (same folder where is README.md)

```shell
./gradlew bootRun 
```

or with predefined `spring.profiles.active` 
```shell
./gradlew bootRun -Dspring.profiles.active=dev
```
Alternatively, you can use Docker to run the application along with PostgreSQL and Redis:

```shell
docker-compose -f compose.yaml up --build
```
This will set up and start the application, PostgreSQL, and Redis containers.
</details>


## API Endpoints

### Crypto Price Endpoints
<details><summary>Extra info (click to expand)</summary>
* GET /api/v1/crypto/{symbol}/oldest

Retrieves the oldest price for a specific cryptocurrency.

Response:
```json
{
  "timestamp": 1641009600000,
  "symbol": "BTC",
  "price": 46813.21
}
```
* GET /api/v1/crypto/{symbol}/newest

Retrieves the newest price for a specific cryptocurrency.

* GET /api/v1/crypto/{symbol}/price/{type}

Retrieves either the minimum or maximum price for the specified cryptocurrency within a given time frame (in months).

#### Parameters:

    - symbol: The cryptocurrency symbol (e.g., BTC, ETH).
    - type: The type of price to retrieve, either "min" or "max".
    - months: The number of months to calculate the price range for.

* GET /api/v1/crypto/{symbol}/normalized-range

Retrieves the normalized range (i.e., (max - min) / min) for the given cryptocurrency between the specified start and end dates.

#### Parameters:

    - start: The start date for the range (in ISO date-time format).
    - end: The end date for the range (in ISO date-time format).

* GET /api/v1/crypto/{symbol}/sorted-normalized-range

Retrieves a sorted list of cryptocurrencies based on their normalized range between the specified start and end dates.

* GET /api/v1/crypto/{symbol}/highest-normalized-range

Retrieves the cryptocurrency with the highest normalized range for a specific day.

#### Parameters:

    - date: The date for the query (in ISO date format).
</details>

### Crypto Currency Endpoints

<details><summary>Extra info (click to expand)</summary>

* POST /api/cryptocurrencies

Adds a new cryptocurrency to the system.

Request body:
```json
{
"symbol": "BTC",
"name": "Bitcoin"
}
```
* DELETE /api/cryptocurrencies/{symbol}

Removes a cryptocurrency by its symbol.

* GET /api/cryptocurrencies

Retrieves a list of all cryptocurrencies in the system.
</details>


### CSV File Upload Endpoints

<details><summary>Extra info (click to expand)</summary>

* POST /api/csv/upload

Uploads a CSV file containing cryptocurrency prices to the system.

Request body: 
A file containing the cryptocurrency prices.

* GET /api/csv/upload/all

Uploads all available CSV files to the system.
</details>

### Containerization with Docker
To run the application with Docker and Docker Compose:

Build the Docker images:

```shell
docker-compose -f compose.yaml build

```

Start the containers:

```shell
docker-compose -f compose.yaml up
```
This will set up the cryptoapp, PostgreSQL, and Redis containers, and the application will be available at http://localhost:8080.

## Potential Enhancements
* Scalability: Currently, the system only supports five cryptocurrencies. In the future, the service can be extended to dynamically handle a larger number of cryptos.
* Data Validation: Implement validation to ensure that the uploaded CSV files are properly formatted before processing.
* Advanced Rate Limiting: Enhance the rate limiting mechanism to prevent abuse and ensure fair usage of the API.
* Machine Learning Integration: Use machine learning algorithms to recommend cryptocurrencies based on trends and patterns in historical data.
* Kubernetes Integration: Deploy the application on a Kubernetes cluster for better scalability and resilience.
