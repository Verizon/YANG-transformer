/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.services;

public class TransactionStatus {

	private int counter = 0;
	private String name;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int value) {
		counter = value;
	}

	public void resetCounter() {
		counter = 0;
	}

	public void addCounter() {
		counter = counter + 1;
	}

}
