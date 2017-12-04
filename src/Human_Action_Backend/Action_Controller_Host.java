package Human_Action_Backend;

import java.util.List;

import Data.Move;
import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.RoundWinException;
import GUIs.GameUI;
import Modelling.BoardModel;
import Network.Network_Server;
import PieceMaker.PieceMakerGUI;
import Players.Player;
import Statistics_Backend.StatisticsController;

public class Action_Controller_Host extends ActionControllerLocalSingle {
	private Network_Server network;

	public Action_Controller_Host(GameUI gUI, Player player1, Player player2, boolean speed, int rounds,
			Network_Server serv, boolean isFirst) {
		super(gUI, player1, player2, speed, rounds, isFirst);

		network = serv;
		network.send(boardModel);
		System.out.println("1 " + !boardModel.isFirstTurn());
		if (!isFirst) {// TODO replace this mess
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

	@Override
	public void firstMove() throws RoundWinException, DrawException, GameWonException {
		System.out.println("2 " + network != null);
		if (network != null) {
			System.out.println("3 " + !boardModel.getAvailibleMoves().isEmpty());
			if (!boardModel.getAvailibleMoves().isEmpty()) {
				network.send(boardModel);
				Move move = network.getMove();
				if (move != null) {
					boardModel.movePiece(move.getOrigin());
					boardModel.movePiece(move.getTarget());
				}
				boardModel.updateGUI();
			} else {
				Move move = boardModel.getUndoList().get(boardModel.getUndoList().size() - 1);
				move.swap();
				boardModel.setEnemyMove(move);
			}
		}
	}

	@Override
	public void nextMove() throws RoundWinException, DrawException, GameWonException {
		boardModel.updateGUI();
		if (network.isClosed()) {
			System.out.println("Lost the client");
			resign();
		} else if (!boardModel.getCurrentPlayer().equals(player1)) {
			Thread x = new Thread(new Runnable() {
				public void run() {

					network.send(boardModel);
					Move move = network.getMove();
					try {
						if (move != null) {
							if (!boardModel.movePiece(move.getTarget()))
								AlertUser("Client sent invalid move");
							nextMove();
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

				}
			});
			x.start();

		}
	}

	@Override
	public List<String> loadStart() {
		return null;
	}

	@Override
	public ActionController saveButtonClick() {
		return null;
	}

	@Override
	public boolean undoButtonClick() {
		return false;
	}

	@Override
	public void GameWin() {
		// determin who won by them having a higher score and display
		// congratulation and add score
		if (boardModel.getfirstPlayer().getScore() > boardModel.getsecondPlayer().getScore()) {
			boardModel.alertUser("Game Won, Winner : " + boardModel.getfirstPlayer().getUsername() + "!");
			StatisticsController.SaveUser(boardModel.getfirstPlayer().getUsername(),
					boardModel.getsecondPlayer().getUsername());
		} else {
			boardModel.alertUser("Game Won, Winner : " + boardModel.getsecondPlayer().getUsername() + "!");
			StatisticsController.SaveUser(boardModel.getsecondPlayer().getUsername(),
					boardModel.getfirstPlayer().getUsername());
		}
		network.send(new BoardModel(null, null, null, -1));
		boardModel.resign();
		network.close();
	}

	@Override
	public void resign() {
		boardModel.resign();
		network.close();
	}

	@Override
	public void inGameResign() {
		if (boardModel != null) {
			boardModel.inGameResign();
		}
		network.close();
	}
}
