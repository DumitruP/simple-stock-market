package com.jpmorgan.stock.market.dao;

import static com.jpmorgan.stock.market.domain.enums.TradeIndicator.BUY;
import static com.jpmorgan.stock.market.domain.enums.TradeIndicator.SELL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jpmorgan.stock.market.domain.Trade;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/stock-dao.xml"})
public class TradeDaoTest {
	
	@Autowired
	private TradeDao tradeDao;
	
	@Test
	public void testGetAllElements() {
		List<Trade> trades = tradeDao.getAll();
		assertThat("Number of trades must be 0", trades.size(), equalTo(0));
	}
	
	@Test
	public void testAddNewTrades() {
		int initialSize = tradeDao.getAll().size();
		Trade trade1 = new Trade("POP", 5, BUY, new BigDecimal("2.75"));
		tradeDao.add(trade1);
		List<Trade> trades = tradeDao.getAll();
		assertThat("Number of trades must be 1", trades.size(), equalTo(initialSize+1));
		
		Trade trade2 = new Trade("GIN", 10, SELL, new BigDecimal("1.00"));
		tradeDao.add(trade2);
		trades = tradeDao.getAll();
		assertThat("Number of trades must be 2", trades.size(), equalTo(initialSize+2));
	}
	
	@Test
	public void testGetTradesWithStockSymbol_Found() {
		List<Trade> trades = tradeDao.findByStockSymbol("RRR");
		assertThat("Number of trades with stock symbol TEA must be 0", trades.size(), is(0));
		Trade newTrade = new Trade("TEA", 5, BUY, new BigDecimal("2.75"));
		tradeDao.add(newTrade);
		trades = tradeDao.findByStockSymbol("TEA");
		assertThat("Number of trades with stock symbol TEA after one was added must be 1", trades.size(), is(1));
	}

}
