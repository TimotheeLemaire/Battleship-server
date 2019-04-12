package com.arzolt.battleship;

import java.util.List;

public class fleetMessageBody {
	private Player player;
	private List<Ship> fleet;
		
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public List<Ship> getFleet() {
		return fleet;
	}
	public void setFleet(List<Ship> fleet) {
		this.fleet = fleet;
	}
	
}
