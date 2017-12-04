package Human_Action_Backend;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import GUIs.GameUI;
import Modelling.BoardModel;
import Modelling.SpeedBoardModel;
import Players.Player;

public class ActionControllerLocalTwo extends ActionController{

	
	public ActionControllerLocalTwo(GameUI gUI, Player player1, Player player2, boolean speed, int rounds){
		super(player1, player2);
		if(speed){
			boardModel = new SpeedBoardModel(gUI, player1, player2, this, rounds);
		} else {
			boardModel = new BoardModel(gUI, player1, player2, rounds);
		}
	}
	
	public ActionControllerLocalTwo(BoardModel model,Player Player1, Player Player2){
		super(Player1, Player2);
		boardModel = model;
	}
	protected void makeNewBoard(boolean winner) {

		// firstPlayer shows who had the first move this turn.
		// winner is who won the round
		String winnerName;
		boolean order = true;
		if (boardModel.getCurrentPlayer() == boardModel.getfirstPlayer()) {
			winnerName = boardModel.getfirstPlayer().getUsername();
		} else {
			winnerName = boardModel.getsecondPlayer().getUsername();
			order = false;
		}
		boardModel.alertUser("Round won by " + winnerName);
		boolean reverse = JOptionPane.showConfirmDialog(new JFrame(),
				"Would you like to use the reversed piece layout?", "Kamisado", JOptionPane.YES_NO_OPTION) == 0;
		if (order) {
			remakeBoard(player2, player1, reverse);
		} else {
			remakeBoard(player1, player2, reverse);
		}

		
	}
}
