package Players;

import Data.Move;
import Modelling.BoardModel;

public class Human implements Player {
	private String username;
	private int score;

	public Human(String name) {
		username = name;
		score = 0;
	}

	public Human(String name, int score) {
		username = name;
		this.score = score;
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
		// we want something like this
		// board.getUI().otherPlayer();
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
