package Data;


import java.awt.Color;

import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.RoundWinException;
import Players.Player;

public interface Model_Interface extends java.io.Serializable  {
	
	/*Returns a copy of the board array rather than the actual copy to prevent errors.*/
	public Piece[][] getBoardArray();
	public void setBoardArray(Piece[][] board);
	
	public void movePiece(Move m, boolean isPlayerOne) throws RoundWinException, DrawException, GameWonException;
	void firstTurnSetup(Color[] COLOUR_ORDER, Player firstPlayer, Player secondPlayer);
	Model duplicate();
	public void resetMoves();
}
