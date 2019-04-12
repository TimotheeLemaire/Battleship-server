package com.arzolt.battleship;

public class Hit {
	public enum Result { HIT, MISS, SINK }
	
	private Position target;
	private Result result;
	private Ship sunk;
	public Position getTarget() {
		return target;
	}
	public void setTarget(Position target) {
		this.target = target;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public Ship getSunked() {
		return sunk;
	}
	public void setSunked(Ship sunk) {
		this.sunk = sunk;
	}
}
