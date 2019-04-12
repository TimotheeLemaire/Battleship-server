package com.arzolt.battleship;

import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Game {
	private final long id;
	private final Player p1;
	private Player p2;
	private List<Ship> fleet1;
	private List<Ship> fleet2;
	private List<Hit> hitsP1 = new LinkedList<Hit>();
	private List<Hit> hitsP2 = new LinkedList<Hit>();
	
	public Game(long id, Player p1) {
		this.id = id;
		this.p1 = p1;
	}
	
	public Boolean placementFinished() {
		return (fleet1!=null&&fleet2!=null);
	}

	public long getId() {
		return id;
	}

	public Player getP1() {
		return p1;
	}

	public Player getP2() {
		return p2;
	}

	public void setP2(Player p2) {
		this.p2 = p2;
	}

	public List<Ship> getFleet1() {
		return fleet1;
	}

	public void setFleet1(List<Ship> fleet1) {
		this.fleet1 = fleet1;
	}

	public List<Ship> getFleet2() {
		return fleet2;
	}

	public void setFleet2(List<Ship> fleet2) {
		this.fleet2 = fleet2;
	}

	public List<Hit> getHitsP1() {
		return hitsP1;
	}

	public void setHitsP1(List<Hit> hitsP1) {
		this.hitsP1 = hitsP1;
	}

	public List<Hit> getHitsP2() {
		return hitsP2;
	}

	public void setHitsP2(List<Hit> hitsP2) {
		this.hitsP2 = hitsP2;
	}
	
	//----------------utilities------------------
	public Ship shipDamaged(Position pos,Player player) {
		
		List<Ship> fleetToCheck;
		
		if(player.equals(p1)) {
    		fleetToCheck=fleet2;
    	} else if (player.equals(p2)) {
    		fleetToCheck=fleet1;
    	} else {
    		return null;    	
    	}
		
		for(Ship ship : fleetToCheck) {
			if(matchShip(pos, ship)) {
				return ship;
			}
		}
		return null;
		
	}
	
	public boolean matchShip(Position position, Ship ship) {
    	for (Position pos : ship.toPositionList()) {
			if(pos.equals(position)) {
				return true;
			}
		}
    	return false;
    }
	
    public boolean isShipSunk(Ship ship) {
    	
    	List<Hit> hitsToCheck;
    	
    	if(fleet1.contains(ship)) {
    		hitsToCheck = hitsP2;
    	} else if (fleet2.contains(ship)) {
    		hitsToCheck = hitsP1;
    	} else {
    		return false;
    	}
    	
    	Boolean matched;
    	Boolean result = true;
    	
    	for (Position pos : ship.toPositionList()) {
			matched = false;
			for (Hit hit : hitsToCheck) {
				if(pos.equals(hit.getTarget())){
					matched = true;
					break;
				}
			}
			if(!matched) {
				result = false;
			}
		}
    	return result;

    }
    
    public Player playerTurn() {
		//P1 turn
	    if(hitsP1.size()==hitsP2.size()) {
			return p1;
	    } else if(getHitsP1().size()>getHitsP2().size()) {
			return p2;
		} else {
			return p1;
		}
    }
   
}
