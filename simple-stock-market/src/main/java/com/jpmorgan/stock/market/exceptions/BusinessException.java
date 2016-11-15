package com.jpmorgan.stock.market.exceptions;

public class BusinessException extends Exception {

	/**
	 * Serial ID for BusinessException
	 */
	private static final long serialVersionUID = 3750340253952198493L;

	public BusinessException() {
	}

	public BusinessException(String message) {
		super(message);
	}
}