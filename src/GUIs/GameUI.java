package GUIs;

import Modelling.BoardModel;

public interface GameUI extends ViewUI,  java.io.Serializable{
	public void updateUI(BoardModel boardModel);
	public void displayVictory();
	public void alertUser(String alertStr);
	public void setClock(String str);
	public void resignGame();	//Goes to main menu rather than exit game.
}
