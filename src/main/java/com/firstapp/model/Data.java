package com.firstapp.model;

public class Data {
	private String text;
	private int x1 =0;
	private int y1 =0;
	private int x2 =0;
	private int y2 =0;
	
	private int maxX = 0;
	private int maxY = 0;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getY1() {
		return y1;
	}
	public void setY1(int y1) {
		this.y1 = y1;
	}
	public int getX2() {
		return x2;
	}
	public void setX2(int x2) {
		this.x2 = x2;
	}
	public int getY2() {
		return y2;
	}
	public void setY2(int y2) {
		this.y2 = y2;
	}
	public int getMaxY() {
		maxY = Math.max(y1, y2);
		return maxY;
	}
	
	public int getMaxX() {
		maxX = Math.max(x1, x2);
		return maxX;
	}
}
