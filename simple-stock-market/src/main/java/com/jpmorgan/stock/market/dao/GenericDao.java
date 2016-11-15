package com.jpmorgan.stock.market.dao;

import java.util.List;

public interface GenericDao<T> {
	
	/**
	 * Get all elements
	 * 
	 * @return T
	 */
	public List<T> getAll();
	
	/**
	 * Add new element
	 * 
	 * @param element
	 */
	public void add(T element);

}
