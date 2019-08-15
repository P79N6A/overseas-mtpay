package com.overseas.mtpay.bean.entity;

public class ArrayItem {
	private int realValue;
	private String realValue1;
	private String showValue;
	
	public ArrayItem() {}

	public ArrayItem(int realValue, String showValue) {
		this.realValue = realValue;
		this.showValue = showValue;
	}

	public ArrayItem(String realValue1, String showValue) {
		this.realValue1 = realValue1;
		this.showValue = showValue;
	}

	public String getRealValue1() {
		return realValue1;
	}

	public void setRealValue1(String realValue1) {
		this.realValue1 = realValue1;
	}

	public int getRealValue() {
		return realValue;
	}

	public void setRealValue(int realValue) {
		this.realValue = realValue;
	}

	public String getShowValue() {
		return showValue;
	}

	public void setShowValue(String showValue) {
		this.showValue = showValue;
	}

	@Override
	public String toString() {
		return showValue;
	}
	
	
}
