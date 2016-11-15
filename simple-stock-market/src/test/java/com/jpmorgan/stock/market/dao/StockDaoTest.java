package com.jpmorgan.stock.market.dao;

import static com.jpmorgan.stock.market.domain.enums.StockType.COMMON;
import static com.jpmorgan.stock.market.domain.enums.StockType.PREFERRED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jpmorgan.stock.market.domain.Stock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/stock-dao.xml"})
public class StockDaoTest {
	
	@Autowired
	private StockDao stockDao;
	
	@Test
	public void testGetAllElements() {
		List<Stock> stocks = stockDao.getAll();
		assertThat("Number of stocks must be 5", stocks.size(), equalTo(5));
	}
	
	@Test
	public void testAddNewStocks() {
		int initialSize = stockDao.getAll().size();
		Stock stock1 = new Stock("RRR", COMMON, BigDecimal.TEN, null, new BigDecimal("1.20"));
		Stock stock2 = new Stock("ABR", PREFERRED, BigDecimal.valueOf(5), null, new BigDecimal("2.50"));
		stockDao.add(stock1);
		stockDao.add(stock2);
		List<Stock> stocks = stockDao.getAll();
		assertThat("Number of stocks must be 7", stocks.size(), equalTo(initialSize+2));
	}
	
	@Test
	public void testFindByStockSymbol_Found() {
		Optional<Stock> stock = stockDao.findBySymbol("POP");
		assertThat("Found stock is null", stock.isPresent());
	}
	
	@Test
	public void testFindByStockSymbol_NotFound() {
		Optional<Stock> stock = stockDao.findBySymbol("SSS");
		assertThat("Found stock is not found", !stock.isPresent());
	}

}
