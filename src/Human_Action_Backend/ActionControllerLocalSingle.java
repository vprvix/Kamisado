package Human_Action_Backend;

import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Data.Move;
import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.RoundWinException;
import GUIs.GameUI;
import Modelling.BoardModel;
import Modelling.SpeedBoardModel;
import Players.Ai;
import Players.Player;

public class ActionControllerLocalSingle extends ActionController {

	public ActionControllerLocalSingle(GameUI gUI, Player player1, Player player2, boolean speed, int rounds,
			boolean isFirstTurn) {
		super(player1, player2);
		if (isFirstTurn) {
			if (speed) {
				boardModel = new SpeedBoardModel(gUI, player1, player2, this, rounds);
			} else {
				boardModel = new BoardModel(gUI, player1, player2, rounds);
			}
		} else {
			if (speed) {
				boardModel = new SpeedBoardModel(gUI, player2, player1, this, rounds);
			} else {
				boardModel = new BoardModel(gUI, player2, player1, rounds);
			}
		}
		if (!isFirstTurn) {// TODO replace this mess
			try {
				firstMove();
			} catch (RoundWinException e) {
				if (e.getWinner().getScore() >= boardModel.REQUIRED_SCORE) {
					GameWin();
				} else {
					RoundWin(e.getWinner());
				}
			} catch (DrawException e) {
				RoundDraw();
			} catch (GameWonException e) {
				GameWin();
			}
		}
	}

	public ActionControllerLocalSingle(BoardModel currentModel, Player player1, Player player2) {
		super(player1, player2);
		boardModel = currentModel;
		if (!currentModel.isFirstTurn()) {// TODO replace this mess
			try {
				firstMove();
			} catch (RoundWinException e) {
				RoundWin(e.getWinner());
			} catch (DrawException e) {
				RoundDraw();
			} catch (GameWonException e) {
				GameWin();
			}
		}
	}

	public void firstMove() throws RoundWinException, DrawException, GameWonException {

		if (!boardModel.getAvailibleMoves().isEmpty()) {
			Move move = player2.makeMove(boardModel);
			boardModel.movePiece(move.getOrigin());
			boardModel.movePiece(move.getTarget());
			boardModel.updateGUI();
		} else {
			Move move = boardModel.getUndoList().get(boardModel.getUndoList().size() - 1);
			move.swap();
			boardModel.setEnemyMove(move);
		}

	}

	public void nextMove() throws RoundWinException, DrawException, GameWonException {
		if (!boardModel.getCurrentPlayer().equals(player1)) {
			Move move = player2.makeMove(boardModel);
			boardModel.movePiece(move.getTarget());
			boardModel.updateGUI();
			nextMove();
		}
	}

	@Override
	public boolean undoButtonClick() {
		if (boardModel == null)
			return false;
		if (boardModel.undo()) {
			boardModel.updateGUI();
			return true;
		} else
			return false;
	}

	@Override
	public boolean buttonBoardClick(String buttonName) {
		if (boardModel == null)
			return false;

		if (!boardModel.getCurrentPlayer().equals(player1)) {
			boardModel.alertUser("Not your turn");
			return false;
		}

		// Take the passed button name and extract the x,y coordinates
		StringTokenizer buttonPosition = new StringTokenizer(buttonName, ",");

		int x = Integer.valueOf(buttonPosition.nextToken());
		int y = Integer.valueOf(buttonPosition.nextToken());

		try {
			if (boardModel.movePiece(x, y)) {
				boardModel.updateGUI();

				// TODO: RUN THIS BIT ASYNCHRONOUSLY?
				try {
					nextMove();
				} catch (GameWonException e) {
					GameWin();
				} catch (RoundWinException e) {
					if (e.getWinner().getScore() >= boardModel.REQUIRED_SCORE) {
						GameWin();
					} else {
						RoundWin(e.getWinner());
					}
				} catch (DrawException e) {
					RoundDraw();
				}

			}
		} catch (GameWonException e) {
			GameWin();
		} catch (RoundWinException e) {
			if (e.getWinner().getScore() >= boardModel.REQUIRED_SCORE) {
				GameWin();
			} else {
				RoundWin(e.getWinner());
			}
		} catch (DrawException e) {
			RoundDraw();
		}

		return true;
	}

	protected void makeNewBoard(boolean winner) {

		// firstPlayer shows who had the first move this turn.
		// winner is who won the round
		String winnerName;
		if (boardModel.getCurrentPlayer() == boardModel.getfirstPlayer()) {
			winnerName = boardModel.getfirstPlayer().getUsername();
		} else {
			winnerName = boardModel.getsecondPlayer().getUsername();
		}
		boardModel.alertUser("Round won by " + winnerName);
		boolean reverse = JOptionPane.showConfirmDialog(new JFrame(),
				"Would you like to use the reversed piece layout?", "Kamisado", JOptionPane.YES_NO_OPTION) == 0;
		if (winnerName.equals(player1.getUsername())) {
			remakeBoard(player2, player1, reverse);
		} else {
			remakeBoard(player1, player2, reverse);
		}

		if (boardModel.getCurrentPlayer().equals(player2)) {
			try {
				firstMove();
			} catch (RoundWinException | DrawException | GameWonException e) {
				// Can't happen. This happens at the start of a turn.
			}
		}
	}

	@Override
	public ActionController saveButtonClick() {
		if (boardModel != null) {
			Player AI = new Ai(player2.getUsername(), 2);
			ActionController returnable = new ActionControllerLocalSingle(boardModel, boardModel.getfirstPlayer(), AI);
			return returnable;
		}
		return null;
	}
}
