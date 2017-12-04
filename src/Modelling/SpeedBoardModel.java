package Modelling;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import Data.Move;
import Data.Piece;
import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.RoundWinException;
import GUIs.GameUI;
import Human_Action_Backend.ActionController;
import Human_Action_Backend.Action_Controller_Board;
import Players.Player;

public class SpeedBoardModel extends BoardModel {
	Action_Controller_Board ACI;
	static final ScheduledThreadPoolExecutor EXECUTOR = (ScheduledThreadPoolExecutor) Executors
			.newScheduledThreadPool(1);
	static ClockTimer time;
	final int LENGTH_OF_TIME = 30;

	double I;

	public SpeedBoardModel(GameUI gameUI, Player playerOne, Player playerTwo, Action_Controller_Board ACI, int rounds) {
		super(gameUI, playerOne, playerTwo, rounds);
		type = 1;
		this.ACI = ACI;
		time = new ClockTimer();
		EXECUTOR.submit(time);

	}

	public SpeedBoardModel(GameUI gameUI, Player pl1, Player pl2, ActionController actionController, int required_score,
			Piece[][] boardArray) {
		super(gameUI, pl1, pl2, required_score, boardArray);
		type = 1;
		this.ACI = actionController;
		time = new ClockTimer();
		EXECUTOR.submit(time);
	}

	public synchronized void endGame() {

		this.getNextPlayer().incrementScore(1);
		if (this.getNextPlayer().getScore() >= this.REQUIRED_SCORE) {
			ACI.GameWin();
			this.resign();
		}

		ACI.RoundWin(super.getCurrentPlayer());
		time.abort();
		EXECUTOR.getQueue().clear();
	}

	public boolean movePiece(int x, int y) throws RoundWinException, DrawException, GameWonException {
		boolean returnable = false;
		try {
			returnable = super.movePiece(x, y);
		} catch (RoundWinException | GameWonException e) {
			time.abort();
			throw e;
		}
		if (returnable)
			time.reset();

		return returnable;
	}

	@Override
	public boolean undo() {
		time.reset();
		return super.undo();
	}

	@Override
	public synchronized void inGameResign() {
		time.abort();
		EXECUTOR.getQueue().clear();
		super.inGameResign();
	}

	@Override
	public synchronized void resign() {
		time.abort();
		EXECUTOR.getQueue().clear();
		super.resign();
	}

	public class ClockTimer implements Runnable {

		@Override
		public void run() {
			DecimalFormat df = new DecimalFormat("#.0");
			I = LENGTH_OF_TIME;
			while (I > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);

					getGameUI().setClock("Time: " + df.format(I));
				} catch (InterruptedException e) {
					abort();
				}
				I -= 0.1;
			}

			if (I <= 0 && !(I < -9)) {
				alertUser("Time's run out!" + I);
				endGame();
			}

		}

		public void abort() {
			I = -10;
		}

		public void reset() {
			I = LENGTH_OF_TIME;
		}
	}
}
