package com.arzolt.battleship;

import java.util.ArrayList;
import java.util.Arrays;

public class Position {
	
	public static final ArrayList<Character> LETTERS = new ArrayList<Character>(
			Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'));
	
	private char letter;
	private int number;
	
	Position(){}
	
	Position(char letter, int number){
		this.letter=letter;
		this.number=number;
	}
	
	public char getLetter() {
		return letter;
	}
	
	public int getNumber() {
		return number;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}

	public void setNumber(char number) {
		this.number = number;
	}
	
	public boolean equals(Position p) {
		return p.getLetter() == this.getLetter() && p.getNumber() == this.getNumber();
	}
}
