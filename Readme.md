


# binance-telegram-bot

Brief description of your project.

## Overview

This project integrates various technologies including Azure Key Vault, Azure SQL Database, and Telegram Bots to manage cryptocurrency trading. It reacts to signals sent via Telegram, processes trading events, and employs schedulers to handle different states and errors in the trading process.

## Features

- **Azure Key Vault Integration**: Securely manages application secrets.
- **Azure SQL Database**: Utilizes Azure's cloud-based SQL database for data persistence.
- **Telegram Bot Integration**: Receives and processes trading signals sent from a Telegram channel.
- **Event-Driven Architecture**: Uses `IncomingTradingSignalEvent`, `NewBuyOrderEvent`, and `NewSellOrderEvent` for handling trading operations.
- **Schedulers**: Implements various schedulers to manage different states and error handling in the trading process.

## Configuration

The application is configurable via application properties, including:

- Database credentials and URL.
- Azure Key Vault settings for secure secret management.
- Telegram Bot token for Telegram integration.
- JPA and Hibernate settings for database object-relational mapping.
- Investment and scheduling configurations for trading logic.

```
# Example configuration properties
spring.datasource.url=${AZURE_DB_URL}
spring.datasource.username=${AZURE_DB_USERNAME}
spring.datasource.password=${AZURE_DB_PASSWORD}
...
```

## Getting Started

### Prerequisites

- Java JDK (version 11)
- Maven (for dependency management and building the project)
- An Azure account with Key Vault and SQL Database
- A Telegram account for setting up the bot

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/ozgen/binance-telegram-bot.git
   ```
2. Navigate to the project directory and install dependencies:
   ```
   cd binance-telegram-bot
   mvn install
   ```
3. Configure your application properties with your Azure and Telegram credentials.

4. Run the application:
   ```
   mvn spring-boot:run
   ```

## Usage


## Contributing


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

