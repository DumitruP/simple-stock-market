package com.jpmorgan.stock.market.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.log4j.Logger;

import com.jpmorgan.stock.market.dao.StockDao;
import com.jpmorgan.stock.market.dao.TradeDao;
import com.jpmorgan.stock.market.domain.Stock;
import com.jpmorgan.stock.market.domain.Trade;
import com.jpmorgan.stock.market.exceptions.BusinessException;
import com.jpmorgan.stock.market.service.SimpleStockMarketService;

public class SimpleStockMarketServiceImpl implements SimpleStockMarketService {
	
	final static Logger LOG = Logger.getLogger(SimpleStockMarketServiceImpl.class);
	
	private StockDao stockDao;
	
	private TradeDao tradeDao;
	
	private int digitsAfterPoint;
	
	private int lastMinutes;

	/*
	 * @see com.jpmorgan.stock.market.service.SimpleStockMarketService#calculateDividendYield(java.lang.String, java.math.BigDecimal)
	 */
	@Override
	public BigDecimal calculateDividendYield(String stockSymbol, BigDecimal price) throws BusinessException{
		LOG.info("Calculating the dividend yield...");
		BigDecimal dividend = null;
		try {
			dividend = calculateDividend(stockSymbol, price);
		} catch(BusinessException be) {
			LOG.error("While calculating the dividend yield an error occured: " + be.getMessage());
			throw be;
		}
		LOG.info("Dividend Yield was calculated for stock with symbol [" + stockSymbol +"].");
		return dividend;
	}

	private BigDecimal calculateDividend(String stockSymbol, BigDecimal price) throws BusinessException {
		BigDecimal dividend = BigDecimal.ZERO;
		// check the arguments
		if (stockSymbol == null) {
			throw new BusinessException("Stock symbol cannot be null.");
		}
		if (price == null) {
			throw new BusinessException("Stock price cannot be null.");
		}
		if (price.compareTo(BigDecimal.ZERO) != 1) {
			throw new BusinessException("Stock price must be greater than zero.");
		}
		Optional<Stock> stock = stockDao.findBySymbol(stockSymbol);
		if (!stock.isPresent()) {
			throw new BusinessException("Stock with symbol [" + stockSymbol + "] is not present in the market.");
		}
		// calculate the dividend depending on stock type
		switch (stock.get().getType()) {
			case COMMON: 
				dividend = stock.get().getLastDividend().divide(price, digitsAfterPoint, RoundingMode.HALF_UP);
				break;
			case PREFERRED: 
				dividend = stock.get().getFixedDividend().multiply(stock.get().getParValue())
						.divide(price, digitsAfterPoint, RoundingMode.HALF_UP);
				break;
			default: 
				throw new RuntimeException("Unknown stock type");
		}
		return dividend;
	}

	@Override
	public BigDecimal calculatePriceEarningsRatio(String stockSymbol, BigDecimal price) throws BusinessException{
		LOG.info("Calculating the Price-Earnings Ratio...");
		BigDecimal priceEarningsRatio = null;
		try {
			BigDecimal dividend = calculateDividend(stockSymbol, price);
			if (dividend.equals(BigDecimal.ZERO.setScale(digitsAfterPoint))) {
				throw new BusinessException("Dividend calculated must not be equal to zero.");
			}
			priceEarningsRatio = price.divide(dividend).setScale(digitsAfterPoint);
		} catch(BusinessException be) {
			LOG.error("While calculating the P/E Ratio an error occured: " + be.getMessage());
			throw be;
		}
		LOG.info("Price-Earnings Ratio was calculated for stock with symbol [" + stockSymbol +"].");
		return priceEarningsRatio;
	}

	/*
	 * @see com.jpmorgan.stock.market.service.SimpleStockMarketService#recordTrade(com.jpmorgan.stock.market.domain.Trade)
	 */
	@Override
	public void recordTrade(Trade trade) throws BusinessException {
		LOG.info("Recording a new trade..");
		try {
			if (trade == null) {
				throw new BusinessException("Trade record cannot be null.");
			}
			// check all the values of the trade
			if (trade.getStockSymbol() == null) {
				throw new BusinessException("Stock symbol in a trade must not be null.");
			}
			if (trade.getQuantity() != null && trade.getQuantity() <= 0) {
				throw new BusinessException("Quantity of shares in a trade must be greater than zero.");
			}
			if (trade.getIndicator() == null) {
				throw new BusinessException("Trade indicator cannot be null.");
			}
			if (trade.getPrice() != null && trade.getPrice().compareTo(BigDecimal.ZERO) != 1) {
				throw new BusinessException("Price of a share in a trade must be greater than zero.");
			}
			
			Optional<Stock> stock = stockDao.findBySymbol(trade.getStockSymbol());
			if (!stock.isPresent()) {
				throw new BusinessException("A trade must be associated with a stock.");
			}
			// add a new trade
			tradeDao.add(trade);
		} catch (BusinessException be) {
			LOG.error("While recording a new trade an error occured: " + be.getMessage());
			throw be;
		}
		LOG.info("A new trade for stock with symbol [" + trade.getStockSymbol() + "] was recorded.");
	}

	/*
	 * @see com.jpmorgan.stock.market.service.SimpleStockMarketService#calculateVolumeWeightedStockPriceInPast5Minutes(java.lang.String)
	 */
	@Override
	public BigDecimal calculateVolumeWeightedStockPriceInPast5Minutes(String stockSymbol) throws BusinessException{
		LOG.info("Calculating Volume Weighted Stock Price based on trades in last " + lastMinutes +" minutes.");
		BigDecimal stockPrice = null;
		try {
			if (stockSymbol == null) {
				throw new BusinessException("Stock symbol cannot be null.");
			}
			stockPrice = calculateVolumeWeightedStockPrice(stockSymbol, Duration.ofMinutes(5));
		} catch(BusinessException be) {
			LOG.error("While calculating Volume Weighted Stock Price an error occured: " + be.getMessage());
			throw be;
		}
		LOG.info("Volume Weighted Stock Price based on trades in last " + lastMinutes 
				+ " minutes for stock with symbol [" + stockSymbol + "was calculated.");
		return stockPrice;
	}
	
	private BigDecimal calculateVolumeWeightedStockPrice(String stockSymbol, Duration pastTime) throws BusinessException{
		List<Trade> trades = tradeDao.findByStockSymbol(stockSymbol);
		List<Trade> filteredTrades = filterTradesByCreationTimeStamp(trades, pastTime);
		BigDecimal totalTradedPrice = BigDecimal.ZERO;
		int totalQuantity = 0;
		for (Trade trade : filteredTrades) {
			// calculate total traded price for a specific stock
			totalTradedPrice = totalTradedPrice.add(trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())));
			// calculate total quantity for a specific stock
			totalQuantity += trade.getQuantity();
		}
		BigDecimal totalStockPrice = BigDecimal.ZERO;
		if (totalQuantity != 0) { 
			totalStockPrice = totalTradedPrice.divide(BigDecimal.valueOf(totalQuantity), digitsAfterPoint, RoundingMode.HALF_UP);
		}
		return totalStockPrice;
	}

	private List<Trade> filterTradesByCreationTimeStamp(List<Trade> trades, Duration pastTime) {
		List<Trade> filteredTrades = null;
		if (pastTime == null) {
			// if pasTime is null then filtering should not be done
			filteredTrades = trades;
		} else {
			LocalDateTime startTimeStamp = LocalDateTime.now().minus(pastTime);
			// filter trades that were created in the specified duration of time
			filteredTrades = trades.stream()
				.filter(x -> x.getRecordCreationTimeStamp().isAfter(startTimeStamp))
				.collect(Collectors.toList());
		}
		return filteredTrades;
	}

	/*
	 * @see com.jpmorgan.stock.market.service.SimpleStockMarketService#calculateGBCEAllShareIndex()
	 */
	@Override
	public BigDecimal calculateGBCEAllShareIndex() throws BusinessException{
		LOG.info("Calculating Global Beverage Corporation Exchange All Share Index...");
		List<Stock> stocks = stockDao.getAll();
		// get a list of prices for all stocks
		List<BigDecimal> stockPrices = new ArrayList<BigDecimal>();
		for (Stock stock : stocks) {
			BigDecimal stockPrice = calculateVolumeWeightedStockPrice(stock.getSymbol(), null);
			if (!stockPrice.equals(BigDecimal.ZERO)) {
				stockPrices.add(stockPrice);
			}
		}
		// create and populate a stock prices array of doubles
		double[] stockPricesArray = new double[stockPrices.size()];
		for (int i=0; i<stockPricesArray.length; i++) {
			stockPricesArray[i] = stockPrices.get(i).doubleValue();
		}
		// return geometric mean of all prices for all stocks 
		BigDecimal geometricMean = BigDecimal.valueOf(StatUtils.geometricMean(stockPricesArray));
		LOG.info("All Share Index was calculated");
		return geometricMean.setScale(digitsAfterPoint, RoundingMode.HALF_UP);
	}

	public void setStockDao(StockDao stockDao) {
		this.stockDao = stockDao;
	}

	public void setTradeDao(TradeDao tradeDao) {
		this.tradeDao = tradeDao;
	}

	public void setDigitsAfterPoint(int digitsAfterFraction) {
		this.digitsAfterPoint = digitsAfterFraction;
	}
	
	public void setLastMinutes(int lastMinutes) {
		this.lastMinutes = lastMinutes;
	}

}
