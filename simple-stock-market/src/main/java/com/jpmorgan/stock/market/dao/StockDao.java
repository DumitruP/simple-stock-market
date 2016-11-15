package com.jpmorgan.stock.market.dao;

import java.util.Optional;

import com.jpmorgan.stock.market.domain.Stock;

public interface StockDao extends GenericDao<Stock> {
	
	/**
	 * Find by stock symbol.
	 * 
	 * @param symbol
	 * @return the {@link Optional<Stock>}
	 */
	Optional<Stock> findBySymbol(String symbol);

}
