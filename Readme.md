
# Binance Telegram Bot

## Introduction
Binance Telegram Bot is an automated trading bot that integrates Binance's trading capabilities with signals received from Telegram channels.
It executes trades on Binance based on signals from Telegram, ensuring automated and timely responses to market changes.

## Features
- Signal Processing
- Trade Execution
- Configurable Settings
- Detailed Logging and Monitoring
- Robust Error Handling

## Getting Started

### Prerequisites
- Java 11+
- Maven
- Binance account
- Telegram account

### Installation
1. Clone the repo: `git clone https://github.com/ozgen/binance-telegram-bot.git`
2. Navigate to the directory: `cd binance-telegram-bot`
3. Build with Maven: `mvn clean install`

### Configuration
Configure the application using environment variables or a `.env` file. Here are the key variables you need to set:

#### Database Configuration
- `spring.datasource.url`: URL to the Azure database
- `spring.datasource.username`: Azure database username
- `spring.datasource.password`: Azure database password

#### Azure Keyvault Configuration
- `azure.keyvault.uri`: Azure keyvault URI
- `azure.keyvault.client-id`: Azure keyvault client ID
- `azure.keyvault.client-key`: Azure keyvault client key
- `azure.keyvault.tenant-id`: Azure keyvault tenant ID

#### Telegram Bot Configuration
- `bot.telegram.token`: Telegram bot token
- `bot.telegram.enabled`: Enable Telegram bot (`true` or `false`)

#### JPA and Hibernate Configuration
- `spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation`: Hibernate setting for LOB creation
- `spring.jpa.hibernate.ddl-auto`: Hibernate DDL auto strategy (`create`, `create-drop`, `validate`, `update`)

#### Bot Investment Configuration
- `app.bot.investment.currency`: Investment currency (e.g., `BTC`)
- `app.bot.investment.amount`: Total investment amount
- `app.bot.investment.perAmount`: Amount per trade
- `app.bot.investment.currencyRate`: Currency rate for conversion (e.g., `BTCUSD`)
- `app.bot.investment.percentageInc`: Percentage increase for buying
- `app.bot.investment.profitPercentage`: Profit percentage target

#### Bot Schedule Configuration
- `app.bot.schedule.buyError`: Schedule interval for buy error in ms
- `app.bot.schedule.sellError`: Schedule interval for sell error in ms
- `app.bot.schedule.insufficient`: Schedule interval for insufficient balance in ms
- `app.bot.schedule.notInRange`: Schedule interval for not in range status in ms
- `app.bot.schedule.tradingSignal`: Schedule interval for trading signals in ms
- `app.bot.schedule.openSellOrder`: Schedule interval for open sell orders in ms
- `app.bot.schedule.openBuyOrder`: Schedule interval for open buy orders in ms
- `app.bot.schedule.monthBefore`: Number of months before for date calculations

#### New Configuration for Telegram Error Reporting
- `bot.telegram.error.enabled`: Set to `true` to enable error reporting on Telegram.

### Running the Bot
Start the bot with:
```bash
java -jar target/binance-telegram-bot-0.0.1-SNAPSHOT.jar
```


## Usage
The bot monitors specified Telegram channels for trading signals and executes corresponding trades on Binance. Here's an example of a trading signal the bot can process:

```
Trading Signal Example:
NKNBTC

ENTRY: 0.00000260 - 0.00000290

TP1: 0.00000315
TP2: 0.00000360
TP3: 0.00000432
TP4: 0.00000486
TP5: 0.00000550
TP6: 0.00000666
TP7: 0.00000741

STOP: Close weekly below 0.00000240
```

## Test Coverage

### Running Tests
Execute the test suite using Maven:
```bash
mvn test
```

### Coverage Reporting
After running the tests, generate a coverage report using JaCoCo (Java Code Coverage Library):
```bash
mvn jacoco:report
```
The coverage report can be found in `target/site/jacoco/index.html`. Open it in a web browser to view detailed coverage statistics.

## License
This project is licensed under the MIT License - see [LICENSE.md](LICENSE.md) for details.

## Acknowledgments
- Binance API
- Telegram API

---
