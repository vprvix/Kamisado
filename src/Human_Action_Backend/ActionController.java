package Human_Action_Backend;

import java.util.List;
import java.util.StringTokenizer;

import Exceptions.DrawException;
import Exceptions.GameWonException;
import Exceptions.RoundWinException;
import FileHandling.BoardIO;
import GUIs.GameUI;
import Modelling.BoardModel;
import Modelling.SpeedBoardModel;
import Players.Player;
import Statistics_Backend.StatisticsController;

public abstract class ActionController implements Action_Controller_GUI, Action_Controller_Board, java.io.Serializable {
	BoardModel boardModel;
	Player player1;
	Player player2;

	public ActionController() {
	}

	public ActionController(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;

	}

	// PLAYER 1 MUST ALWAYS BE THE HOST.
	@Override
	public boolean buttonBoardClick(String buttonName) {
		if (boardModel == null)
			return false;

		// Take the passed button name and extract the x,y coordinates
		StringTokenizer buttonPosition = new StringTokenizer(buttonName, ",");

		int x = Integer.valueOf(buttonPosition.nextToken());
		int y = Integer.valueOf(buttonPosition.nextToken());

		try {
			if (boardModel.movePiece(x, y)) {
				boardModel.updateGUI();

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

	@Override
	public boolean undoButtonClick() {

		if (boardModel == null)
			return false;

		boardModel.alertUser("Hail hydra - only allowed in AI battles");
		return false;

	}

	@Override
	public boolean exitButtonClick() {
		boardModel.exit();
		return false;
	}

	protected abstract void makeNewBoard(boolean winner);

	protected void remakeBoard(Player pl1, Player pl2, boolean reverse) {
		if (boardModel.getType() == 0) {
			if (boardModel == null)
				boardModel = new BoardModel(boardModel.getGameUI(), pl1, pl2, boardModel.REQUIRED_SCORE);
			else
				boardModel = new BoardModel(boardModel.getGameUI(), pl1, pl2, boardModel.REQUIRED_SCORE,
						boardModel.getBoardArray());
		} else {
			if (boardModel == null)
				boardModel = new SpeedBoardModel(boardModel.getGameUI(), pl1, pl2, this, boardModel.REQUIRED_SCORE);
			else
				boardModel = new SpeedBoardModel(boardModel.getGameUI(), pl1, pl2, this, boardModel.REQUIRED_SCORE,
						boardModel.getBoardArray());
		}

		if (reverse)
			boardModel.reverseOrder();
		boardModel.updateGUI();

	}

	@Override
	public void RoundDraw() {
		boardModel.alertUser("Round Draw");
		makeNewBoard(!boardModel.isFirstPlayer());
	}

	@Override
	public void RoundWin(Player winner) {
		if (winner.getScore() >= boardModel.REQUIRED_SCORE) {
			this.GameWin();
		} else if (winner.getUsername().equals(this.player1.getUsername())) {
			makeNewBoard(boardModel.isFirstPlayer());
		} else {
			makeNewBoard(!boardModel.isFirstPlayer());
		}
	}

	// TODO: ADD TO THE PLAYER'S STATISTIICS
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
		
		resign();
		boardModel = null;

	}

	@Override
	public void resign() {
		boardModel.resign();
	}

	@Override
	public void inGameResign() {
		if (boardModel != null) {
			boardModel.inGameResign();
		}

	}

	@Override
	public ActionController saveButtonClick() {
		boardModel.setGameUI(null);
		return this;

	}

	public void resetUI(GameUI gameUI) {
		if (boardModel != null) {
			boardModel.setGameUI(gameUI);
			boardModel.updateGUI();
		}
	}
	// TODO Get Working

	public boolean loadButtonClick(GameUI gameUI, String FileADDR) {

		ActionController data = BoardIO.loadGame(FileADDR);
		if (data != null) {
			this.player1 = data.player1;
			this.player2 = data.player2;
			this.boardModel = data.boardModel;
			boardModel.setGameUI(gameUI);
			boardModel.updateGUI();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<String> loadStart() {
		return BoardIO.fileList();
	}

	@Override
	public void AlertUser(String str) {
		boardModel.alertUser(str);

	}
}
