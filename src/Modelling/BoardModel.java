package Modelling;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Data.BoardColours;
import Data.Model;
import Data.Move;
import Data.Piece;
import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.NoPossibleMovesException;
import Exceptions.RoundWinException;
import GUIs.GameUI;
import Players.Player;

public class BoardModel implements java.io.Serializable {
	private Model board;
	private Player firstPlayer;
	private Player secondPlayer;
	private boolean isFirstPlayer; // To find the next player
	private boolean isFirstTurn; // Different rules for turn 1
	private boolean undoing;
	private boolean selected;
	private List<Move> undoList;
	private ArrayList<Move> possibleMoves;
	private GameUI gameUI;
	private Move currentMove;
	public BoardColours colourHolder;
	public int type; // what type of mode its in
	public int REQUIRED_SCORE = 2;

	public BoardModel(GameUI gameUI, Player playerOne, Player playerTwo, int ScoreTarget) {
		type = 0;
		REQUIRED_SCORE = ScoreTarget;
		this.gameUI = gameUI;
		isFirstPlayer = true;
		isFirstTurn = true;
		selected = false;

		this.firstPlayer = playerOne;
		this.secondPlayer = playerTwo;
		possibleMoves = new ArrayList<>(); // get First move initialization.
		undoList = new ArrayList<>();
		colourHolder = new BoardColours();
		if (JOptionPane.showConfirmDialog(new JFrame(), "Random board?", "Kamisado", JOptionPane.YES_NO_OPTION) == 0) {
			colourHolder.shift();
		}

		board = new Model(firstPlayer, secondPlayer);
		InitializeRows();
		updateGUI();
	}

	public BoardModel(GameUI gameUI, Player pl1, Player pl2, int ScoreTarget, Piece[][] boardArray) {
		type = 0;
		REQUIRED_SCORE = ScoreTarget;
		this.gameUI = gameUI;
		isFirstPlayer = true;
		isFirstTurn = true;
		selected = false;

		this.firstPlayer = pl1;
		this.secondPlayer = pl2;
		possibleMoves = new ArrayList<>(); // get First move initialization.
		undoList = new ArrayList<>();
		colourHolder = new BoardColours();
		if (JOptionPane.showConfirmDialog(new JFrame(), "Random board?", "Kamisado", JOptionPane.YES_NO_OPTION) == 0) {
			colourHolder.shift();
		} else {
			colourHolder = new BoardColours();

		}
		board = new Model(firstPlayer, secondPlayer);
		board.setBoardArray(boardArray);
		InitializeRows();
		updateGUI();
	}

	public boolean isFirstPlayer() {
		return isFirstPlayer;
	}

	public void resign() {
		gameUI.resignGame();

	}
	// Everything in here must be saved.

	public Piece[][] getBoardArray() {
		return board.getBoardArray();
	}

	public Model getModel() {
		return board;
	}

	private Player getPlayer(boolean firstPlayer) {
		if (isFirstPlayer)
			return this.firstPlayer;
		else
			return secondPlayer;
	}

	public Player getCurrentPlayer() {
		return getPlayer(isFirstPlayer);
	}

	public Player getNextPlayer() {
		return getPlayer(!isFirstPlayer);
	}

	public void updateGUI() {
		if (gameUI != null)
			gameUI.updateUI(this);
	}

	public boolean movePiece(int[] target) throws RoundWinException, DrawException, GameWonException {
		return movePiece(target[0], target[1]);

	}

	/* Returns true if the GUI needs to be updated. */
	public boolean movePiece(int x, int y) throws RoundWinException, DrawException, GameWonException {
		boolean returnable = false;
		boolean actualFirstPlayer = isFirstPlayer;
		Piece[][] boardArray = board.getBoardArray();
		if (isFirstTurn) {

			if (boardArray[x][y] != null && boardArray[x][y].getOwner().equals(firstPlayer)) {
				currentMove = new Move();
				currentMove.setOrigin(x, y);
				selected = true;
				updateGUI();
			} else if (selected && boardArray[x][y] == null) {
				currentMove.setTarget(x, y);
				if (isValidMove(currentMove)) {
					performMove(currentMove);
					isFirstTurn = false;
					returnable = true;
				} else {

				}
			} else {
				alertUser("Select first piece please");
			}
		} else { // We already know what piece is being used as the origin.

			currentMove.setTarget(x, y);
			if (isValidMove(currentMove)) {

				performMove(currentMove);

				returnable = true;
			} else {
				currentMove.resetTarget();
			}
		}
		if (actualFirstPlayer != isFirstPlayer) {
			board.resetMoves();
		}
		return returnable;

	}

	// check is right player and is valid move
	public boolean isValidMove(Move m) {
		for (Move move : possibleMoves) {
			if (m.equals(move))
				return true;
		}
		alertUser("Invalid move");
		return false;
	}

	private void firstTurnAddition() {
		for (int i = 0; i < 8; i++) {
			try {
				possibleMoves.addAll(addAvailableMoves(i, 7));
			} catch (NoPossibleMovesException e) {

			}
		}
	}

	public BoardColours getColours() {
		return colourHolder;
	}

	// Assumptions:
	// That location holds a piece.
	// That piece belongs to current player.
	//
	// ADDS (doesn't SET) available moves to possibleMoves = to all current
	// available moves (excluding first pass).
	public void setAvailableMoves(int x, int y) throws NoPossibleMovesException {

		possibleMoves = new ArrayList<>();
		possibleMoves.addAll(addAvailableMoves(x, y));

	}

	// Takes a move to search the target square colour for in enemy playerOne's
	// piece set.
	public Move getNextTile(Move m, boolean enemyPlayer) {
		return board.getNextTile(m, enemyPlayer, colourHolder);
	}

	public ArrayList<Move> addAvailableMoves(int x, int y) throws NoPossibleMovesException {
		return addAvailableMoves(x, y, isFirstPlayer, board);
	}

	public ArrayList<Move> addAvailableMoves(int x, int y, boolean isFirstPlayer, Model board)
			throws NoPossibleMovesException {
		int leftX = x;
		int rightX = x;
		int originalX = x;
		int originalY = y;
		boolean middle = true, right = true, left = true;
		boolean canSumo = true;
		Piece[][] boardArray = board.getBoardArray();

		ArrayList<Move> possibleMoves = new ArrayList<Move>();

		int moves = boardArray[x][y].getDistance();

		Move n = new Move();
		n.setOrigin(x, y);

		if (isFirstPlayer)
			n.setTarget(x, y - 1);
		else
			n.setTarget(x, y + 1);
		if (n.getTarget()[1] > 7 || n.getTarget()[1] < 0)
			canSumo = false;

		if (canSumo && canSumo(n)) {
			if (isFirstPlayer) {
				possibleMoves.add(new Move(originalX, originalY, originalX, originalY - 1, isFirstPlayer));
			} else if (!isFirstPlayer) {
				possibleMoves.add(new Move(originalX, originalY, originalX, originalY + 1, isFirstPlayer));
			}
		}
		while ((middle || right || left) && moves > 0) {

			moves--;
			rightX++;
			leftX--;
			if (isFirstPlayer)
				y--;
			else
				y++;

			if (y > 7 || y < 0)
				break;

			if (middle && boardArray[x][y] == null)
				possibleMoves.add(new Move(originalX, originalY, x, y, isFirstPlayer));

			else
				middle = false;

			if (rightX <= 7 && right && boardArray[rightX][y] == null)
				possibleMoves.add(new Move(originalX, originalY, rightX, y, isFirstPlayer));
			else
				right = false;

			if (leftX >= 0 && left && boardArray[leftX][y] == null)
				possibleMoves.add(new Move(originalX, originalY, leftX, y, isFirstPlayer));
			else
				left = false;
		}

		if (possibleMoves.size() == 0) {

			throw new NoPossibleMovesException("No possible move for piece at " + originalX + "," + originalY);

		}
		return possibleMoves;
	}

	public void exit() {
		resign();
	}

	private boolean inRange(Move x) {
		if (x.getOrigin()[0] == -1 || x.getOrigin()[1] == -1 || x.getTarget()[0] == -1 || x.getTarget()[1] == -1) {
			return false;
		}
		return true;
	}

	public boolean isSumoMove(Move x, Model board) {

		return board.getBoardArray()[x.getTarget()[0]][x.getTarget()[1]] != null
				&& board.getBoardArray()[x.getOrigin()[0]][x.getOrigin()[1]] != null
				&& x.getOrigin()[0] == x.getTarget()[0];
	}

	private boolean canSumo(Move x) {

		// Sanity checks.
		if (!inRange(x) || board == null || board.getBoardArray() == null)
			return false;

		// Make sure that the 2 pieces exist and are on the same x coord.
		int i = 0;
		boolean sumo = isSumoMove(x, board);

		// Check if there is a piece in front of the sumo matching the player's
		// movement options
		if (sumo) {

			// If it's a normal piece, a sumo push isn't available

			if (board.getBoardArray()[x.getOrigin()[0]][x.getOrigin()[1]].getDistance() == 7)
				return false;

			sumo = isFirstPlayer && x.getOrigin()[1] == (x.getTarget()[1] + 1)
					|| !isFirstPlayer && x.getOrigin()[1] == (x.getTarget()[1] - 1);

			if (isFirstPlayer)
				i = (x.getTarget()[1] + 1);
			else
				i = (x.getTarget()[1] - 1);
		} else {
			// If sumo is false then no push may happen.
			return false;
		}

		int numberOfPieces = 0;

		while (true) {
			if (i == 8 || i == -1) {
				return false;
			}

			if (board.getBoardArray()[x.getTarget()[0]][i] != null)
				numberOfPieces++;
			else
				break;
			if (isFirstPlayer)
				i--;
			else
				i++;

		}
		return board.getBoardArray()[x.getOrigin()[0]][x.getOrigin()[1]].canSumo(numberOfPieces);

	}

	public void doSumoMove(Move x, Model board, boolean isFirstPlayer) {
		board.getBoardArray()[x.getOrigin()[0]][x.getOrigin()[1]].moveSumo();

		if (isFirstPlayer)
			x.setTarget(x.getOrigin()[0], x.getOrigin()[1] - 1);
		else
			x.setTarget(x.getOrigin()[0], x.getOrigin()[1] + 1);

		int lastSlot = 0;
		boolean found = true;

		for (int i = x.getOrigin()[1]; found;) {// Find the empty slot in this
												// push.
			if (isFirstPlayer)
				i--;
			else
				i++;

			if (board.getBoardArray()[x.getOrigin()[0]][i] == null) {
				lastSlot = i;
				found = false;
			}
		}

		// Start at the last slot. Stop when the sumo was moved and left an
		// empty place.
		while (board.getBoardArray()[x.getOrigin()[0]][x.getOrigin()[1]] != null) {
			Move shuffle = new Move();
			shuffle.setTarget(x.getOrigin()[0], lastSlot);

			if (!isFirstPlayer)
				lastSlot--;
			else
				lastSlot++;

			shuffle.setOrigin(x.getOrigin()[0], lastSlot);
			try {
				board.movePiece(shuffle, isFirstPlayer);
			} catch (DrawException | RoundWinException | GameWonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// We're moving an existing piece to a null and will replace the previous
	// location with a null too.
	public void performMove(Move x) throws RoundWinException, GameWonException {
		boolean draw = false;

		try {

			// If a sumo move is possible right now by the respective player.
			if (canSumo(x)) {
				doSumoMove(x, board, isFirstPlayer);
				isFirstPlayer = !isFirstPlayer;
			} else
				board.movePiece(x, isFirstPlayer);

		} catch (DrawException e) {
			playerWon();
			draw = true;
		}
		currentMove.setPlayer(isFirstPlayer);
		if (!undoing)
			undoList.add(currentMove.duplicate());

		if (isFirstPlayer) {
			if (currentMove.getTarget()[1] == 0) {
				playerWon();
			}
		} else if (currentMove.getTarget()[1] == 7) {
			playerWon();
		}

		isFirstPlayer = !isFirstPlayer;
		try {
			setEnemyMove(x);
		} catch (DrawException e) {
			playerWon();
			draw = true;
		}
		currentMove.resetTarget();

	}

	public void playerWon() throws RoundWinException, GameWonException {

		if (gameConclusion()) {
			throw new GameWonException("Game won");
		} else {
			int x = currentMove.getTarget()[0];
			int y = currentMove.getTarget()[1];

			getCurrentPlayer().incrementScore(board.getBoardArray()[x][y].getValue());

			board.getBoardArray()[x][y].sumoUp();

			InitializeRows();

			throw new RoundWinException("Round won", getCurrentPlayer());
		}

	}

	/*
	 * Sets the enemy move based on whatever move was put in
	 */

	public void setEnemyMove(Move m) throws DrawException {
		Color c = colourHolder.getColor(m.getTarget()[0], m.getTarget()[1]); // tile
																				// color.
		Piece[][] boardArray = board.getBoardArray();
		List<Piece> checkedPieces = new ArrayList<Piece>();

		boolean truePlayer = isFirstPlayer;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				// If this piece matches the colour and is the current players
				if (boardArray[x][y] != null && boardArray[x][y].getOwner().equals(getCurrentPlayer())
						&& boardArray[x][y].getColour().equals(c)) {

					try {
						setAvailableMoves(x, y);
						currentMove.setOrigin(x, y);
						currentMove.resetTarget();
						return;
					} catch (NoPossibleMovesException e) {
						// That piece has no moves. Replace C with its color
						if (checkedPieces.size() > 15)
							throw new DrawException("Somehow got every piece into a draw, congratulations."); // Even
																												// possible?
																												// If
																												// so,
																												// harder
																												// than
																												// winning.
						for (Piece p : checkedPieces) {
							if (p.equals(boardArray[x][y]))
								throw new DrawException("Cycle of pieces!");
						}
						checkedPieces.add(boardArray[x][y]);
						if (boardArray[x][y].getOwner().equals(getCurrentPlayer())) {
							isFirstPlayer = !isFirstPlayer;
						}
						c = colourHolder.getColor(x, y);
						x = 0;
						y = 0;
					}

				}
			}
		}

		isFirstPlayer = truePlayer;

	}

	public boolean gameConclusion() {
		if (firstPlayer.getScore() >= REQUIRED_SCORE || secondPlayer.getScore() >= REQUIRED_SCORE) {
			return true;
		} else
			return false;

	}

	public void loadGame(String identifier) {
	}

	private void InitializeRows() {
		boolean necessary = true;

		if (board != null) {
			for (Piece p[] : board.getBoardArray()) {
				for (Piece piece : p) {
					if (piece == null)
						continue;
					if (piece.getDistance() != 7) {
						necessary = false;
						break;
					}
				}
			}
		}

		if (necessary)
			board = new Model(colourHolder.DEFAULT_COLOUR_ORDER, firstPlayer, secondPlayer);
		else
			board = new Model(colourHolder.DEFAULT_COLOUR_ORDER, firstPlayer, secondPlayer, board.getBoardArray());

		firstTurnAddition();
	}

	public void reverseOrder() {
		Color[] reverseOrder = new Color[colourHolder.DEFAULT_COLOUR_ORDER.length];
		int x = 0;
		for (int i = colourHolder.DEFAULT_COLOUR_ORDER.length - 1; i >= 0; i--) {
			reverseOrder[x] = colourHolder.DEFAULT_COLOUR_ORDER[i];
			x++;
		}
		board = new Model(reverseOrder, firstPlayer, secondPlayer, board.getBoardArray());
		firstTurnAddition();
	}

	public Player getfirstPlayer() {
		return firstPlayer;
	}

	public Player getsecondPlayer() {
		return secondPlayer;
	}

	public void alertUser(String string) {
		JOptionPane.showMessageDialog(null, string);
	}

	public ArrayList<Move> getAvailibleMoves() {
		return possibleMoves;
	}

	public int[] getOrigin() {
		if (currentMove != null)
			return currentMove.getOrigin();

		int[] returnable = { 7, 0 };
		return returnable;
	}

	public List<String> pack() {
		List<String> returnable = new ArrayList<String>();
		Piece[][] boardArray = board.getBoardArray();

		String pieceState = "";
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (boardArray[x][y] == null) {
					pieceState += ",#";
				} else {
					pieceState += "," + boardArray[x][y].getColour().getRGB() + "<"
							+ boardArray[x][y].getOwner().getUsername();

				}
			}
		}
		String undoString = "";
		for (Move urrentMove : undoList) {
			undoString = undoString + urrentMove.getOrigin()[0] + "<" + urrentMove.getOrigin()[1] + "<"
					+ urrentMove.getTarget()[0] + "<" + urrentMove.getTarget()[1] + "<" + urrentMove.isFirstPlayer()
					+ ",";
		}
		int[] CM = currentMove.getOrigin();

		returnable.add(pieceState);
		returnable.add(firstPlayer.getUsername() + "," + firstPlayer.getScore());
		returnable.add(secondPlayer.getUsername() + "," + secondPlayer.getScore());
		returnable.add(getCurrentPlayer().getUsername());
		returnable.add("" + REQUIRED_SCORE);
		returnable.add("" + type);
		returnable.add("" + isFirstPlayer);
		returnable.add("" + isFirstTurn);
		returnable.add(undoString);
		returnable.add("" + CM[0] + "," + CM[1]); // CURRENT MOVE
		returnable.add("" + undoing);
		returnable.add("" + selected);
		return returnable;
	}

	public void inGameResign() {// here for speedboardmodel...we broke LSP?

	}

	public boolean isFirstTurn() {
		return isFirstTurn;
	}

	public int getType() {
		return type;
	}

	public List<Move> getUndoList() {
		return undoList;
	}

	// TODO Make work

	// TODO Make work
	public boolean undo() {
		boolean returnable = false;
		if (undoList.size() >= 2) {
			Move m = undoList.get(undoList.size() - 1);
			undoList.remove(undoList.size() - 1);
			Move m2 = undoList.get(undoList.size() - 1);
			undoList.remove(undoList.size() - 1);

			if (undoMove(m))
				if (undoMove(m2)) {
					if (m.isFirstPlayer() == m2.isFirstPlayer()) {
						boolean playerSwapped = false;
						while (!playerSwapped) {
							if (undoList.size() - 1 >= 0) {

								m2 = undoList.get(undoList.size() - 1);
								undoList.remove(undoList.size() - 1);

								if (undoMove(m2))
									returnable = true;
								if (m.isFirstPlayer() != m2.isFirstPlayer()) {
									playerSwapped = true;
								}
							} else {
								playerSwapped = true;
							}
						}
					}
				}
		}
		this.updateGUI();
		if (undoList.size() == 0) {
			selected = false;
			isFirstTurn = true;
			undoing = false;
			isFirstPlayer = true;
			board.firstTurnSetup(colourHolder.COLOUR_ORDER, firstPlayer, secondPlayer);
			InitializeRows();
		}
		return returnable;
	}

	private boolean undoMove(Move m) {
		undoing = true;
		m.swap();
		try {
			performMove(m);
			currentMove.setOrigin(m.getTarget().clone());
			undoing = false;
			return true;
		} catch (RoundWinException | GameWonException e) {

		}
		return false;

	}

	public BoardModel duplicate() {
		return null;
	}

	public void displayBoard() {
		Piece[][] boardArray = getBoardArray();

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (boardArray[x][y] != null)
					System.out.print(boardArray[x][y].toString() + ",");
				else
					System.out.print("000,");
			}
			System.out.println("");
		}
	}

	public GameUI getGameUI() {
		return gameUI;
	}

	public void setGameUI(GameUI object) {
		gameUI = object;
	}
}
