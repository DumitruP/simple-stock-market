package com.jpmorgan.stock.market.domain;

import java.math.BigDecimal;

import com.jpmorgan.stock.market.domain.enums.StockType;

public class Stock {
	
	private String symbol;
	
	private StockType type;
	
	private BigDecimal lastDividend;
	
	private BigDecimal fixedDividend;
	
	private BigDecimal parValue;
	
	@SuppressWarnings("unused")
	private Stock() {
	}
	
	public Stock(String symbol, StockType type, BigDecimal lastDividend, 
			BigDecimal fixedDividend, BigDecimal parValue) {
		this.symbol = symbol;
		this.type = type;
		this.lastDividend = lastDividend;
		this.fixedDividend = fixedDividend;
		this.parValue = parValue;
	}

	public String getSymbol() {
		return symbol;
	}

	public StockType getType() {
		return type;
	}
	
	public BigDecimal getLastDividend() {
		return lastDividend;
	}

	public BigDecimal getFixedDividend() {
		return fixedDividend;
	}

	public BigDecimal getParValue() {
		return parValue;
	}
	
}
