package com.jpmorgan.stock.market.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jpmorgan.stock.market.dao.TradeDao;
import com.jpmorgan.stock.market.domain.Trade;

public class TradeDaoImpl implements TradeDao {

	List<Trade> tradeRecords;
	
	private TradeDaoImpl() {
		tradeRecords = new ArrayList<Trade>();
	}
	
	/*
	 * @see com.jpmorgan.stock.market.dao.GenericDao#getAll()
	 */
	@Override
	public List<Trade> getAll() {
		return tradeRecords;
	}

	/*
	 * @see com.jpmorgan.stock.market.dao.GenericDao#add(java.lang.Object)
	 */
	@Override
	public void add(Trade trade) {
		tradeRecords.add(trade);
	}

	/*
	 * @see com.jpmorgan.stock.market.dao.TradeDao#findByStockSymbol(java.lang.String)
	 */
	@Override
	public List<Trade> findByStockSymbol(String stockSymbol) {
		return tradeRecords.stream()
				.filter(x -> x.getStockSymbol().equals(stockSymbol))
				.collect(Collectors.toList());
	}

	/*
	 * @see com.jpmorgan.stock.market.dao.TradeDao#clear()
	 */
	@Override
	public void clear() {
		tradeRecords.clear();		
	}

}
