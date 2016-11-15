package com.jpmorgan.stock.market.service;

import java.math.BigDecimal;

import com.jpmorgan.stock.market.domain.Trade;
import com.jpmorgan.stock.market.exceptions.BusinessException;

public interface SimpleStockMarketService {
	
	/**
	 * Calculate the dividend yield
	 * 
	 * @param stockSymbol
	 * @param price
	 * @return the dividend yield
	 * @throws BusinessException
	 */
	public BigDecimal calculateDividendYield(String stockSymbol, BigDecimal price)  throws BusinessException;
	
	/**
	 * Calculate the Price-Earnings Ratio
	 * 
	 * @param stockSymbol
	 * @param price
	 * @return the P/E Ratio
	 * @throws BusinessException
	 */
	public BigDecimal calculatePriceEarningsRatio(String stockSymbol, BigDecimal price)  throws BusinessException;
	
	/**
	 * Record a trade
	 * 
	 * @param trade
	 * @throws BusinessException
	 */
	public void recordTrade(Trade trade) throws BusinessException;
	
	/**
	 * Calculate the Volume Weighted Stock Price in past 5 minutes
	 * 
	 * @param stockSymbol
	 * @return the stock price in past 5 minutes
	 * @throws BusinessException
	 */
	public BigDecimal calculateVolumeWeightedStockPriceInPast5Minutes(String stockSymbol) throws BusinessException;
	
	/**
	 * Calculate GBCE All Share Index
	 * 
	 * @return the All Share Index
	 * @throws BusinessException
	 */
	public BigDecimal calculateGBCEAllShareIndex() throws BusinessException;

}
