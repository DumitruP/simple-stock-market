# Super Simple Stock Market
Super Simple Stock Market is a small application that operates with trades and stocks.
## Task requirements
##### 1. The Global Beverage Corporation Exchange is a new stock market trading in drinks companies.
          a. Your company is building the object-oriented system to run that trading.
          b. You have been assigned to build part of the core object model for a limited phase 1.
##### 2. Provide the source code for an application that will:
          a. For a given stock:
                i.   Given any price as input, calculate the dividend yield
                ii.  Given any price as input, calculate the P/E Ratio
                iii. Record a trade, with timestamp, quantity, buy or sell indicator and price
                iV.  Calculate Volume Weighted Stock Price based on trades in past 5 minutes
          b. Calculate the GBCE All Share Index using the geometric mean of the Volume Weighted Stock Price for 
             all stocks
## Implementation
              The project was developed in Java 8 using Maven as project management tool and Spring framework for 
          Dependency Injection and IoC. Both are easy to use and help to develop an organized code of production 
          quality. 
              The design of the application is pretty simple which includes a "service" - SimpleStockMarketService 
          and two DAO objects TradeDao and StockDao that have access to the trades and stocks in memory. Applied 
          Data Access Object pattern allows in future development to substitute the current implementation with 
          in memory data to accessing external datasources(like files or databases) with less refactoring effort.
              An finally the functionality is fully tested using junit and hamcrest libraries. 
## Build & test
##### Please run the following command to build the project and run all the tests:
          mvn clean install
