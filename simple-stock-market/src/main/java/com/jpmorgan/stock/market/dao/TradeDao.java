package com.jpmorgan.stock.market.dao;

import java.util.List;

import com.jpmorgan.stock.market.domain.Trade;

public interface TradeDao extends GenericDao<Trade> {
	
	/**
	 * Find trades by stock symbol.
	 * 
	 * @param stockSymbol
	 * @return the {@link List<Trade>}
	 */
	public List<Trade> findByStockSymbol(String stockSymbol);
	
	/**
	 * Clear all elements.
	 * 
	 */
	public void clear();

}
