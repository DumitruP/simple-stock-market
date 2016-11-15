package com.jpmorgan.stock.market.service;

import static com.jpmorgan.stock.market.domain.enums.TradeIndicator.BUY;
import static com.jpmorgan.stock.market.domain.enums.TradeIndicator.SELL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jpmorgan.stock.market.dao.TradeDao;
import com.jpmorgan.stock.market.domain.Trade;
import com.jpmorgan.stock.market.exceptions.BusinessException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class SimpleStockMarketServiceTest {
	
	@Autowired
	private SimpleStockMarketService stockMarketService;
	
	@Autowired
	private TradeDao tradeDao;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testCalculatedDividendYield_Successful() throws BusinessException {
		// calculate dividend for a common stock
		BigDecimal dividend1 = stockMarketService.calculateDividendYield("POP", BigDecimal.valueOf(4));
		assertThat("Dividend is calculated wrong", dividend1, equalTo(new BigDecimal("0.02")));
		
		// calculate dividend for a preferred stock
		BigDecimal dividend2 = stockMarketService.calculateDividendYield("GIN", BigDecimal.valueOf(0.5));
		assertThat("Dividend is calculated wrong", dividend2, equalTo(new BigDecimal("0.04")));
	}
	
	@Test
	public void testCalculatedDividendYield_StockSymbolNull_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Stock symbol cannot be null"));
		stockMarketService.calculateDividendYield(null, BigDecimal.valueOf(4));
	}
	
	@Test
	public void testCalculatedDividendYield_PriceNull_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Stock price cannot be null"));
		stockMarketService.calculateDividendYield("POP", null);
	}
	
	@Test
	public void testCalculatedDividendYield_InexistingSymbol_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(containsString("is not present in the market"));
		stockMarketService.calculateDividendYield("RRR", BigDecimal.valueOf(4.6));
	}
	
	@Test
	public void testCalculatePriceEarningsRatio_Successful() throws BusinessException {
		// calculate P/E ratio for common stock
		BigDecimal dividend1 = stockMarketService.calculateDividendYield("ALE", BigDecimal.valueOf(2.2));
		assertThat("Dividend is calculated wrong", dividend1, equalTo(new BigDecimal("0.10")));
		
		// calculate P/E ratio for preferred stock
		BigDecimal dividend2 = stockMarketService.calculateDividendYield("GIN", BigDecimal.valueOf(0.55));
		assertThat("Dividend is calculated wrong", dividend2, equalTo(new BigDecimal("0.04")));
	}
	
	@Test
	public void testCalculatePriceEarningsRatio_StockSymbolNull_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Stock symbol cannot be null"));
		stockMarketService.calculatePriceEarningsRatio(null, BigDecimal.valueOf(1.22));
	}
	
	@Test
	public void testCalculatePriceEarningsRatio_DividendZero_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Dividend calculated must not be equal to zero"));
		stockMarketService.calculatePriceEarningsRatio("GIN", BigDecimal.valueOf(4.5));
	}
	
	@Test
	public void testRecordTrade_Successful() throws BusinessException {
		prepareTestWithTrades();
		assertThat("Number of trades is incorrect", tradeDao.getAll().size() == 5);
		// add a new trade
		Trade trade1 = new Trade("POP", 24, SELL, new BigDecimal("2.20"));
		stockMarketService.recordTrade(trade1);
		assertThat("Number of trades is incorrect", tradeDao.getAll().size() == 6);
		// add another trade
		Trade trade2 = new Trade("POP", 12, BUY, new BigDecimal("1.20"));
		stockMarketService.recordTrade(trade2);
		assertThat("Number of trades is incorrect", tradeDao.getAll().size() == 7);
	}
	
	@Test
	public void testRecordTrade_TradeNull_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Trade record cannot be null"));
		stockMarketService.recordTrade(null);
	}
	
	@Test
	public void testRecordTrade_SymbolNull_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Stock symbol in a trade must not be null"));
		Trade trade = new Trade(null, 24, SELL, new BigDecimal("2.20"));
		stockMarketService.recordTrade(trade);
	}
	
	@Test
	public void testRecordTrade_QuantityMinus_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Quantity of shares in a trade must be greater than zero"));
		Trade trade = new Trade("POP", -23, SELL, new BigDecimal("2.20"));
		stockMarketService.recordTrade(trade);
	}
	
	@Test
	public void testRecordTrade_TradeIndicatorNull_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Trade indicator cannot be nul"));
		Trade trade = new Trade("POP", 20, null, new BigDecimal("2.20"));
		stockMarketService.recordTrade(trade);
	}
	
	@Test
	public void testRecordTrade_PriceMinus_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Price of a share in a trade must be greater than zero"));
		Trade trade = new Trade("POP", 20, SELL, new BigDecimal("-2.20"));
		stockMarketService.recordTrade(trade);
	}
	
	@Test
	public void testRecordTrade_NotAssociatedWithStock_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("A trade must be associated with a stock"));
		Trade trade = new Trade("RRR", 20, SELL, new BigDecimal("2.20"));
		stockMarketService.recordTrade(trade);
	}
	
	@Test
	public void testCalculateStockPrice_Successful() throws BusinessException {
		prepareTestWithTrades();
		
		// calculate stock price for GIN which was only once traded
		BigDecimal ginStockPrice = stockMarketService.calculateVolumeWeightedStockPriceInPast5Minutes("GIN");
		assertThat("Stock price for GIN is incorrect", ginStockPrice, equalTo(new BigDecimal("1.00")));
		
		// calculate stock price for POP which was traded twice
		BigDecimal popStockPrice = stockMarketService.calculateVolumeWeightedStockPriceInPast5Minutes("POP");
		assertThat("Stock price for POP is incorrect", popStockPrice, equalTo(new BigDecimal("2.13")));
		
		// calculate stock price for POP which was traded twice, but first trade is 30 min ago form now
		BigDecimal teaStockPrice = stockMarketService.calculateVolumeWeightedStockPriceInPast5Minutes("TEA");
		assertThat("Stock price for POP is incorrect", teaStockPrice, equalTo(new BigDecimal("1.20")));
		
		// calculate stock price for JOE which was not traded
		BigDecimal joeStockPrice = stockMarketService.calculateVolumeWeightedStockPriceInPast5Minutes("JOE");
		assertThat("Stock price for JOE is incorrect", joeStockPrice, equalTo(BigDecimal.ZERO));
	}
	
	@Test
	public void testCalculateStockPrice_Negative() throws BusinessException {
		thrown.expect(BusinessException.class);
		thrown.expectMessage(startsWith("Stock symbol cannot be null"));
		stockMarketService.calculateVolumeWeightedStockPriceInPast5Minutes(null);
	}
	
	@Test
	public void testCalculateAllShareIndex_Successful() throws BusinessException {
		prepareTestWithTrades();
		
		// calculate all share index for trades recorded by this time
		// it is calculated as geometric mean of [1.00, 2.13, 1.44] 
		BigDecimal allShareIndex = stockMarketService.calculateGBCEAllShareIndex();
		assertThat("AllShareIndex is incorrect", allShareIndex, equalTo(new BigDecimal("1.45")));
	}
	
	private void prepareTestWithTrades(){
		Trade trade1 = new Trade("GIN", 35, SELL, new BigDecimal("1.00"));
		Trade trade2 = new Trade("POP", 10, BUY, new BigDecimal("2.50"));
		Trade trade3 = new Trade("POP", 140, BUY, new BigDecimal("2.10"));
		Trade trade4 = new Trade("TEA", 20, SELL, new BigDecimal("1.50"));
		trade4.setRecordCreationTimeStamp(LocalDateTime.now().minus(Duration.ofMinutes(30)));
		Trade trade5 = new Trade("TEA", 5, SELL, new BigDecimal("1.20"));
		tradeDao.clear();
		tradeDao.add(trade1);
		tradeDao.add(trade2);
		tradeDao.add(trade3);
		tradeDao.add(trade4);
		tradeDao.add(trade5);
	}
}
