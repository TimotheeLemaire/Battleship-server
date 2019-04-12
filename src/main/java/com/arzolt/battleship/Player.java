package com.arzolt.battleship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
	private String name;
	
	public Player() {
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(Player player) {
		return (this.name.compareTo(player.getName()));
	}
	
	public boolean equals(Player player) {
		return this.getName().equals(player.getName());		
	}
}
