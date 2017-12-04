package Players;

import Data.Move;
import Modelling.BoardModel;
import Network.Network_Server;

public class Player_Client implements Player{
	private int score;
	private String username;
	public Player_Client(String user){
		username = user;
		score = 0;
	}
	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void incrementScore(int value) {
		score += value;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public Move makeMove(BoardModel board) {
		return null;
	}

}
