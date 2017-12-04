package Players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Data.Model;
import Data.Move;
import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.NoPossibleMovesException;
import Exceptions.RoundWinException;
import Modelling.BoardModel;

public class Ai implements Player {
	private int difficulty;
	private int score;
	private String username;
	private BoardModel board;
	private boolean firstPlayer;
	private int WIN = 110, LOSS = 1000, MOVE = 0, DRAW = 0;

	public Ai(String username, int difficulty) {
		this.difficulty = difficulty;
		score = 0;
		this.username = username;
	}

	public Ai(String username, int difficulty, int score) {
		this.difficulty = difficulty;
		this.score = score;
		this.username = username;
	}

	public Ai(int score, int difficulty) {
		this.username = randomuser();
		this.score = score;
		this.difficulty = difficulty;
	}

	public String randomuser() {
		return "Greg";
	}

	public void setDifficulty(int value) {
		this.difficulty = value;
	}

	public int getDifficulty() {
		return this.difficulty;
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
		this.board = board; // the original board
		this.firstPlayer = board.isFirstPlayer(); // the original player
		return generateAppropriateMove(board.getAvailibleMoves());
	}

	// Sorts the best
	private Move generateAppropriateMove(List<Move> possibleMoves) {
		// Get a default move to act off of.
		int max;

		max = evaluate(possibleMoves.get(0), firstPlayer, difficulty, board.getModel());

		ArrayList<Move> bestMoves = new ArrayList<Move>();
		bestMoves.add(possibleMoves.get(0));

		// Evaluate all the other moves to see if they're better.
		for (Move m : possibleMoves) {
			// How good is the current move?
			int temp;

			temp = evaluate(m, firstPlayer, difficulty, board.getModel());

			// If it's better than our current best, let's use that instead of
			// our others.
			if (temp > max) {
				System.out.println(m + " " + temp);
				max = temp;
				bestMoves.clear();

			}

			// Store all of our best moves here for a choice.
			if (temp == max) {
				bestMoves.add(m);
			}
		}

		// Return a good move!
		if (bestMoves.size() > 0) {
			int random = (int) new Random().nextInt(bestMoves.size());
			return bestMoves.get(random);
		}
		return bestMoves.get(0);

	}

	// Evaluates a move based on its outcome and the outcomes of its possible i
	// descendant moves.
	private int evaluate(Move m, boolean isPlayerOne, int i, Model microWaterboard) {

		boolean sumod = false;

		if (microWaterboard == null)
			return 0;
		Model microBoard = microWaterboard.duplicate();
		m.setPlayer(isPlayerOne);

		if (i == 0) { // Max depth
			return 0;
		}

		if (board.isSumoMove(m, microBoard)) {
			board.doSumoMove(m, microBoard, isPlayerOne);

			isPlayerOne = !isPlayerOne;
			sumod = true;

		}

		if (!sumod && (m.getTarget()[1] == 0 || m.getTarget()[1] == 7)) // Win
																		// condition
		{

			if (firstPlayer == isPlayerOne) {
				return i * WIN * microBoard.getBoardArray()[m.getOrigin()[0]][m.getOrigin()[1]].getValue();
			} else {
				return i * LOSS * microBoard.getBoardArray()[m.getOrigin()[0]][m.getOrigin()[1]].getValue();
			}
		}

		int moveValue = MOVE;

		// Move the piece on the model that we have.
		try {
			if (!sumod)
				microBoard.movePiece(m, isPlayerOne);
		} catch (RoundWinException | DrawException | GameWonException e1) {

		}

		// !!! Set up for the next evaluation
		// Get the next tile if m is carried out and the player is flipped.
		Move move = microBoard.getNextTile(m, !isPlayerOne, board.getColours());
		List<Move> moves;

		try {
			// Get all the moves possible if
			if (!sumod) {
				moves = board.addAvailableMoves(move.getOrigin()[0], move.getOrigin()[1], !isPlayerOne, microBoard);
				for (Move enemyMove : moves) {
					microBoard.resetMoves();
					moveValue += -1 * evaluate(enemyMove, !isPlayerOne, i - 1, microBoard.duplicate());
				}
			}
		} catch (NoPossibleMovesException e) { // Multi-move turn
			moveValue += samePlayerMove(move, microBoard, isPlayerOne, i);

		}

		if (sumod)
			moveValue += samePlayerMove(move, microBoard, isPlayerOne, i);


		return moveValue;

	}

	private int samePlayerMove(Move move, Model microBoard, boolean isPlayerOne, int i) {

		int moveValue = 0;
		ArrayList<Move> moves = new ArrayList<Move>();
		move.swap();
		Move newMove = microBoard.getNextTile(move, isPlayerOne, board.getColours());

		try {
			moves = board.addAvailableMoves(newMove.getOrigin()[0], newMove.getOrigin()[1], isPlayerOne, microBoard);
		} catch (NoPossibleMovesException e1) {
			moveValue += DRAW;
			return moveValue;
		}

		for (Move enemyMove : moves) {
			moveValue += evaluate(enemyMove, isPlayerOne, i - 1, microBoard.duplicate());
		}

		return moveValue;
	}
}