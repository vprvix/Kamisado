package Players;

import java.util.UUID;

import Data.Model;
import Data.Move;
import Modelling.BoardModel;

public interface Player extends java.io.Serializable{
	public final UUID id = new UUID(100,100);
	public String getUsername();
	void incrementScore(int value);
	int getScore();
	public Move makeMove(BoardModel board);

	
}
