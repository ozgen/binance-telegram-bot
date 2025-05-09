
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
- Java 21
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
- `app.bot.schedule.sellLater`: Schedule interval for sell later orders in ms
- `app.bot.schedule.monthBefore`: Number of months before for date calculations

#### New Configuration for Telegram Error Reporting
- `bot.telegram.error.enabled`: Set to `true` to enable error reporting on Telegram.

### Running the Bot
Start the bot with:
```bash
java -jar target/binance-telegram-bot-0.0.1-SNAPSHOT.jar
```
## Setup Instructions

To ensure the system functions correctly, follow these setup requirements:

### Telegram Bot Setup
1. **Create a Telegram Bot**: Follow the instructions on [Telegram's official documentation](https://core.telegram.org/bots#creating-a-new-bot) to create a new bot.
2. **Generate a Bot Token**: Use this [guide](https://medium.com/geekculture/generate-telegram-token-for-bot-api-d26faf9bf064) to generate a token for your Telegram bot.

### Error Reporting with Telegram
- Create a new bot for error reporting following the steps above.
- Create a troubleshooting channel on Telegram and add the error bot as an admin.
- Start the system, then send a message to the troubleshooting channel to initialize the error bot correctly.

### Telegram Channel Listener
- Make sure the bot is an admin of the Telegram channel you want to monitor.

### Database Configuration
- This setup uses Azure Key Vault and Azure SQL Database. For local or alternative databases, update the `application.properties` file accordingly.

### Azure Key Vault
1. **Create an Azure Key Vault**: Follow the steps outlined [here](https://azure.github.io/cloud-scale-data-for-devs-guide/get-started-with-java-and-key-vault.html).
2. **Connect to Azure Key Vault**: Use the instructions provided [here](https://learn.microsoft.com/en-us/azure/azure-app-configuration/use-key-vault-references-spring-boot?tabs=yaml) to connect.

### Azure SQL Database
1. **Create an Azure SQL Database**: Detailed instructions can be found [here](https://learn.microsoft.com/en-us/azure/azure-sql/database/single-database-create-quickstart?tabs=azure-portal). Opting for a serverless configuration is cost-effective.
2. **Connect to Azure SQL Database**: Follow these [steps](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/configure-spring-data-jdbc-with-azure-sql-server?tabs=passwordless) to connect.

### Environment Variables
Set up the following environment variables for your application:
```
AZURE_DB_PASSWORD=<db-password>
AZURE_DB_URL=<azure-db-url>
AZURE_DB_USERNAME=<db-user-name>
AZURE_KEYVAULT_CLIENT_ID=<azure-keyvault-client-id>
AZURE_KEYVAULT_CLIENT_KEY=<azure-keyvault-client-key>
AZURE_KEYVAULT_TENANT_ID=<azure-keyvault-tenant-id>
AZURE_KEYVAULT_URL=<keyvault-url>
TELEGRAM_ERROR_TOKEN=<telegram-error-token>
TELEGRAM_TOKEN=<telegram-channel-token>
TELEGRAM_USERNAME=<telegram-bot-username>
TELEGRAM_ERROR_USERNAME=<telegram-error-bot-username>
```
Replace the placeholder values with your actual configuration details.

Ensure all prerequisites are met for the system to run smoothly.

### Running the Bot (Local Profile with Dockerized PostgreSQL)

For local development, you can run the **PostgreSQL database via Docker** and run the **Spring Boot application** locally with the `local` profile.

#### Step 1: Start PostgreSQL via Docker Compose

From the project root, run:

```bash
docker-compose -f docker/docker-compose.yml up -d
```

Make sure your `docker-compose.yml` exposes the correct PostgreSQL port (`5432`) and includes environment variables like:

```yaml
environment:
  POSTGRES_DB: binancebot
  POSTGRES_USER: myuser
  POSTGRES_PASSWORD: mypass
```

#### Step 2: Set Local Environment Variables

Export these variables in your terminal session (or place them in a `.env` file if your tooling supports it):

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/binancebot
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=mypass

# Telegram Bot
BOT_TELEGRAM_TOKEN=<your-telegram-token>
BOT_TELEGRAM_ENABLED=true

# Binance API
BINANCE_API_KEY=<your-binance-api-key>
BINANCE_API_SECRET=<your-binance-api-secret>
```
Example env file can be found in [here](/sample-local.env) 

#### Step 3: Run the Spring Boot App with `local` Profile

Build and run the app:

```bash
mvn clean install
java -Dspring.profiles.active=local -jar target/binance-telegram-bot-0.0.1-SNAPSHOT.jar
```

This setup:
- Connects to the PostgreSQL instance running in Docker
- Loads credentials from local environment variables
- Avoids any cloud integrations not enabled in the `local` profile

---

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


## **Optional Parameters: Invest Amount & Sell Later Strategy**
The bot allows **two optional parameters** in trading signals:
- **INVEST** → Specifies how much to invest in the trade.
- **STRATEGY: SELL_LATER** → Delays selling until a future condition is met.

### ** How It Works**
| Parameter  | Description                                                                                                                                       |
|------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `INVEST`   | Defines the amount allocated for the trade (e.g., `"0.0001 BTC"`). If omitted, the bot will use the default investment settings.                  |
| `STRATEGY` | If set to `SELL_LATER`, the bot will **not** immediately create sell order. Instead, It creates sell order when profit targets (TPs) are reached. |

### **Example Signal with Optional Parameters**
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

# Optional Parameters
INVEST: 0.001  
STRATEGY: SELL_LATER
```

### **Behavior of `SELL_LATER` Strategy**
- Normally, the bot **automatically sells** when a TP level is reached.
- If `SELL_LATER` is set:
    1. The bot **holds the asset** instead of selling at TP levels.
    2. The sell order will be **triggered later** based on other conditions (e.g., price spikes, custom alerts).

### How to Use These Parameters
| Scenario | Expected Bot Behavior                           |
|------------|-------------------------------------------------|
| **No `INVEST` value** | Uses the default investment configuration.      |
| **INVEST set** | Overrides the default investment amount.        |
| **No `SELL_LATER`** | The bot sells at the defined profit percentage. |
| **`SELL_LATER` enabled** | The bot holds the asset and delays selling.     |

---

### **Why Use These Options?**
- **INVEST** → Gives you flexibility to **change investment size per trade**.
- **SELL_LATER** → Useful for **longer-term holds** instead of auto-selling.
---

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
