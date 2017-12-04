package Human_Action_Backend;

import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import Data.Move;
import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.RoundWinException;
import GUIs.GameUI;
import Modelling.BoardModel;
import Network.Client;
import Network.Network_Client;
import Players.Ai;
import Players.Player;

public class Action_ControllerNetwork implements Action_Controller_GUI {
	GameUI gameUI;
	Network_Client NC;
	String remoteUser;
	Thread listen;
	BoardModel currentModel;
	Move newMove;

	public Action_ControllerNetwork(GameUI gUI, String userName) {
		gameUI = gUI;
		NC = new Client();
		String ip = JOptionPane.showInputDialog("Host's address: ", "192.168.1.2");
		NC.connect(ip, userName);
		remoteUser = NC.getUsername();
		currentModel = NC.getModel();
		gameUI.updateUI(currentModel);
		currentModel = null;
		listen();
	}

	@Override
	public boolean buttonBoardClick(String buttonName) {
		if (currentModel != null) {
			StringTokenizer buttonPosition = new StringTokenizer(buttonName, ",");
			int x = Integer.valueOf(buttonPosition.nextToken());
			int y = Integer.valueOf(buttonPosition.nextToken());
			if (!currentModel.isFirstTurn()) {
				for (Move move : currentModel.getAvailibleMoves()) {
					if (move.getTarget()[0] == x && move.getTarget()[1] == y) {

						try {

							if (currentModel.isValidMove(move)) {
								currentModel.movePiece(move.getTarget());
								updateMaster(move);
							} else {
								display("Invalid move");
							}
						} catch (RoundWinException e) {
							if (e.getWinner().getScore() >= currentModel.REQUIRED_SCORE) {
								display("Game winner");
								NC.send(move);
								this.resign();
							} else {
								display("Round Winner");
								updateMaster(move);
							}

						} catch (GameWonException e) {
							display("Game winner");
							NC.send(move);
							this.resign();
						} catch (DrawException e) {
							display("Draw");
							updateMaster(move);
						}

					}
				}

				return true;
			} else {
				if (y == 7) {
					newMove = new Move();
					int[] coord = { x, y };
					newMove.setOrigin(coord);
					try {
						currentModel.movePiece(x, y);
					} catch (RoundWinException | DrawException | GameWonException e) {
					}

					return true;
				} else if (newMove != null) {
					newMove.setTarget(x, y);
					try {
						currentModel.movePiece(x, y);
					} catch (RoundWinException | DrawException | GameWonException e) {
					}
					NC.send(newMove);
					return true;
				} else {
					return false;
				}

			}
		} else {
			display("Not your turn");
			return false;
		}
	}

	private void updateMaster(Move move) {
		NC.send(move);
		gameUI.updateUI(currentModel);
		currentModel = null;
	}

	private void listen() {
		listen = new Thread(new Runnable() {
			public void run() {
				while (NC != null && !NC.isClosed()) {
					currentModel = NC.getModel();
					if (currentModel != null) {
						if (currentModel.getCurrentPlayer() == null) {
							display("This is our darkest hour, Good Bye");
							NC.close();
						} else {
							gameUI.updateUI(currentModel);
							currentModel.setGameUI(gameUI);
						}
					}
				}

			}
		});
		listen.start();
	}

	@Override
	public ActionController saveButtonClick() {
		if (currentModel != null) {
			Player AI = new Ai(remoteUser, 2);
			if (currentModel.getfirstPlayer().getUsername().equals(remoteUser)) {
				ActionController returnable = new ActionControllerLocalSingle(currentModel,
						currentModel.getfirstPlayer(), AI);
				return returnable;
			} else {
				ActionController returnable = new ActionControllerLocalSingle(currentModel,
						currentModel.getsecondPlayer(), AI);
				return returnable;
			}
		}
		display("Needs to be your turn to save");
		return null;
	}

	@Override
	public boolean loadButtonClick(GameUI gameUI, String FileADDR) {
		return false;
	}

	@Override
	public boolean undoButtonClick() {
		return false;
	}

	@Override
	public boolean exitButtonClick() {
		return false;
	}

	@Override
	public List<String> loadStart() {
		return null;
	}

	@Override
	public void resetUI(GameUI gameUI) {
		this.gameUI = gameUI;
		currentModel.setGameUI(gameUI);
	}

	@Override
	public void resign() {
		NC.close();
		if (currentModel != null)
			currentModel = null;
	}

	@Override
	public void inGameResign() {
		NC.close();
	}

	private void display(String str) {
		JOptionPane.showMessageDialog(null, str);

	}

}
