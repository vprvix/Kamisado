package Data;

import java.awt.Color;

import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.RoundWinException;
import Players.Player;

public class Model implements Model_Interface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4721830698945232282L;
	Piece[][] board;
	Player firstPlayer;
	Player secondPlayer;

	public Model(Piece[][] boardArray, Player firstPlayer, Player secondPlayer) {
		Piece[][] board_Array = new Piece[8][8];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (boardArray[x][y] != null)
					board_Array[x][y] = boardArray[x][y].duplicate();
			}
		}
		this.board = board_Array;
		this.firstPlayer = firstPlayer;
		this.secondPlayer = secondPlayer;
	}

	public Model(Color[] COLOUR_ORDER, Player firstPlayer, Player secondPlayer) {
		firstTurnSetup(COLOUR_ORDER, firstPlayer, secondPlayer);
	}

	public Model(Player firstPlayer, Player secondPlayer) {
		firstTurnSetup(BoardColours.DEFAULT_COLOUR_ORDER, firstPlayer, secondPlayer);
	}

	public Model(Color[] COLOUR_ORDER, Player firstPlayer, Player secondPlayer, Piece[][] boardArray) {
		this.firstPlayer = firstPlayer;
		this.secondPlayer = secondPlayer;
		firstTurnSetup(COLOUR_ORDER, firstPlayer, secondPlayer);

		// Let's ignore everything and pretend that we got the boardArray in
		// here just now.
		for (int i = 0; i < 8; i++) {
			Piece inputPieceSet[] = boardArray[i];
			for (int x = 0; x < 8; x++) {
				Piece inputPiece = inputPieceSet[x];
				if (inputPiece == null) {
					continue;
				}
				for (int n = 0; n < 8; n++) {
					Piece boardPieces[] = board[n];
					for (int r = 0; r < 8; r++) {
						Piece boardPiece = boardPieces[r];
						if (boardPiece != null) {
							if (boardPiece.getColour().equals(inputPiece.getColour())
									&& boardPiece.getOwner().equals(inputPiece.getOwner())) {

								board[n][r] = inputPiece.duplicate();

							}
						}
					}
				}
			}
		}
	}

	@Override
	public void resetMoves() {
		for (Piece[] p : board) {
			for (Piece piece : p) {
				if (piece != null) {
					piece.resetSumo();
				}
			}
		}

	}

	@Override
	public Piece[][] getBoardArray() {
		return board.clone();
	}

	@Override
	public void setBoardArray(Piece[][] board) {
		this.board = board.clone();
	}

	@Override
	public void firstTurnSetup(Color[] COLOUR_ORDER, Player firstPlayer, Player secondPlayer) {
		InitializeRows(COLOUR_ORDER, firstPlayer, secondPlayer);
		this.firstPlayer = firstPlayer;
		this.secondPlayer = secondPlayer;
	}

	@Override
	public void movePiece(Move x, boolean isPlayerOne) throws RoundWinException, DrawException, GameWonException {
		Piece p = board[x.getOrigin()[0]][x.getOrigin()[1]];
		board[x.getTarget()[0]][x.getTarget()[1]] = p;
		board[x.getOrigin()[0]][x.getOrigin()[1]] = null;
	}

	@Override
	public Model duplicate() {
		return new Model(board, firstPlayer, secondPlayer);
	}

	private void InitializeRows(Color[] COLOUR_ORDER, Player firstPlayer, Player secondPlayer) {
		board = new Piece[8][8];

		for (int i = 0; i < 8; i++) {
			board[i][0] = new Piece();
			board[i][0].setColour(COLOUR_ORDER[i]);
			board[i][0].setOwner(secondPlayer);
			board[i][7] = new Piece();
			board[i][7].setColour(COLOUR_ORDER[7 - i]);
			board[i][7].setOwner(firstPlayer);
		}
	}

	public Move getNextTile(Move m, boolean isFirstPlayer, BoardColours d) {
		  Color c = d.getColor(m.getTarget()[0], m.getTarget()[1]);
		  // color.

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				// If this piece matches the colour and is the current players
				if (board[x][y] != null && board[x][y].getOwner().equals(getCurrentPlayer(isFirstPlayer))
						&& board[x][y].getColour().equals(c)) {

					Move n = new Move();
					n.setOrigin(x, y);
					return n;
				}

			}
		}
		return null;
	}

	private Player getCurrentPlayer(boolean isFirstPlayer) {
		if (isFirstPlayer) {
			return firstPlayer;
		} else {
			return secondPlayer;
		}
	}

	public String[] toStringArray() {
		String[] returnable = new String[8];
		int x = 0;
		int y = 0;

		for (x = 0; x < 8; x++) {

			for (y = 0; y < 8; y++) {
				if (board[x][y] == null)
					returnable[y] += "-";
				else if (board[x][y].getOwner() == firstPlayer)
					returnable[y] += "/";
				else
					returnable[y] += "\\";
			}
		}
		return returnable;

	}

}
