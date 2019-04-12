package com.arzolt.battleship;

import java.util.ArrayList;
import java.util.List;

public class Ship {
	private Position position;
	private Direction orientation;
	private int size;
	
	public Position getPosition() {
		return position;
	}

	public Direction getOrientation() {
		return orientation;
	}

	public int getSize() {
		return size;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setOrientation(Direction orientation) {
		this.orientation = orientation;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public List<Position> toPositionList(){
        
        int i;
        List<Position> positions = new ArrayList<Position>();
        Position newPosition;

        for(i=0;i<this.size;i++){
            switch(orientation){
            case NORTH:
                newPosition = new Position( 
                		Position.LETTERS.get(Position.LETTERS.lastIndexOf(this.position.getLetter())),
                        this.position.getNumber()-i);
                
                positions.add(newPosition);
                break;
            case SOUTH:
                newPosition = new Position( 
                		Position.LETTERS.get(Position.LETTERS.lastIndexOf(this.position.getLetter())),
                        this.position.getNumber()+i);
                
                positions.add(newPosition);
                break;
            case EAST:
                newPosition = new Position( 
                		Position.LETTERS.get(Position.LETTERS.lastIndexOf(this.position.getLetter())+i),
                        this.position.getNumber());
                
                positions.add(newPosition);
                break;
            case WEST:
                newPosition = new Position( 
                		Position.LETTERS.get(Position.LETTERS.lastIndexOf(this.position.getLetter())-i),
                        this.position.getNumber());
                
                positions.add(newPosition);
                break;
            }
        }
        return positions;
    }
}
