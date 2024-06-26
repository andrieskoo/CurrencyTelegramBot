# Currency Exchange Rate Telegram Bot

This Telegram bot provides the latest exchange rates from banks such as NBP, NBU, PrivatBank, and Monobank.
Additionally, users can configure notifications to receive exchange rates at specified times.

## Features

- **Get Current Exchange Rates**: Retrieve the latest exchange rates from NBP, NBU, PrivatBank, and Monobank.
- **Configure Notifications**: Set up notifications to receive exchange rates at user-defined times.
- **Supported Banks**: NBP, NBU, PrivatBank, Monobank.
- **Supported currency**: UAH, USD, PLN, GBP, EUR.
- **Suported languages**: Ukrainian, Polish, English

## Requirements

- Java 17 or higher
- Maven
- Postgres 16 or higher
- Telegram Bot API token

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/andrieskoo/currency-telegram-bot.git
   cd currency-telegram-bot
   ```
2. **Set up environment variables**  
   Create an application.properties file in the src/main/resources directory and add your Telegram Bot API token:
3. **Build the application**
   ```bash
   mvn clean install
4. **Run the application**

   ```bash
   mvn spring-boot:run
   ```
## Usage
- Start the bot: Find your bot on Telegram and start a chat with it by clicking "Start".
- Get Exchange Rates: Use the command /rates to get the current exchange rates.

## Commands
/start - Start interacting with the bot.  
/rates - Get current exchange rates.

<img alt="ukr.PNG" src="src%2Fmain%2Fresources%2Fukr.PNG" width="128"/>

<img alt="pol.PNG" src="src%2Fmain%2Fresources%2Fpol.PNG" width="128"/>

## License
This project is licensed under the MIT License - see the LICENSE file for details.