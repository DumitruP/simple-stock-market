package com.jpmorgan.stock.market.dao.impl;

import static com.jpmorgan.stock.market.domain.enums.StockType.COMMON;
import static com.jpmorgan.stock.market.domain.enums.StockType.PREFERRED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jpmorgan.stock.market.dao.StockDao;
import com.jpmorgan.stock.market.domain.Stock;

public class StockDaoImpl implements StockDao {
	
	List<Stock> stocks;
	
	private StockDaoImpl() {
		stocks = new ArrayList<Stock>();
		stocks.add(new Stock("TEA", COMMON, BigDecimal.ZERO, null, new BigDecimal("1.00")));
		stocks.add(new Stock("POP", COMMON, new BigDecimal("0.08"), null, new BigDecimal("1.00")));
		stocks.add(new Stock("ALE", COMMON, new BigDecimal("0.23"), null, new BigDecimal("0.60")));
		stocks.add(new Stock("GIN", PREFERRED, new BigDecimal("0.23"), new BigDecimal("0.02"), new BigDecimal("1.00")));
		stocks.add(new Stock("JOE", COMMON, new BigDecimal("0.23"), null, new BigDecimal("2.50")));
	}

	/* 
	 * @see com.jpmorgan.stock.market.dao.GenericDao#getAll()
	 */
	@Override
	public List<Stock> getAll() {
		return stocks;
	}

	/* 
	 * @see com.jpmorgan.stock.market.dao.GenericDao#add(java.lang.Object)
	 */
	@Override
	public void add(Stock stock) {
		stocks.add(stock);		
	}

	/*
	 * @see com.jpmorgan.stock.market.dao.StockDao#findBySymbol(java.lang.String)
	 */
	@Override
	public Optional<Stock> findBySymbol(String symbol) {
		return stocks.stream()
				.filter(x -> x.getSymbol().equals(symbol))
				.findFirst();
	}

}
