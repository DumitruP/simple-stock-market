package com.jpmorgan.stock.market.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jpmorgan.stock.market.domain.enums.TradeIndicator;

public class Trade {
	
	private String stockSymbol;
	
	private Integer quantity;
	
	private TradeIndicator indicator;
	
	private BigDecimal price;
	
	private LocalDateTime recordCreationTimeStamp;

	@SuppressWarnings("unused")
	private Trade() {
	}
	
	public Trade(String stockSymbol, Integer quantity, TradeIndicator indicator, BigDecimal price) {
		this.stockSymbol = stockSymbol;
		this.quantity = quantity;
		this.indicator = indicator;
		this.price = price;
		this.recordCreationTimeStamp = LocalDateTime.now();
	}
	
	public String getStockSymbol() {
		return stockSymbol;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public TradeIndicator getIndicator() {
		return indicator;
	}
	
	public BigDecimal getPrice() {
		return price;
	}

	public LocalDateTime getRecordCreationTimeStamp() {
		return recordCreationTimeStamp;
	}
	
	public void setRecordCreationTimeStamp(LocalDateTime recordCreationTimeStamp) {
		this.recordCreationTimeStamp = recordCreationTimeStamp;
	}

}
