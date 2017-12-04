package Exceptions;

import Players.Player;

@SuppressWarnings("serial")
public class RoundWinException extends Exception {
	Player winner;
	public RoundWinException(String s, Player winner){
		super(s);
		this.winner = winner;
	}
	public Player getWinner(){
		return winner;
	}
}
