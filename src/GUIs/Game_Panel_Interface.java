package GUIs;

import java.awt.event.KeyEvent;


public interface Game_Panel_Interface {
	public void saveRequest();
	public void loadRequest();
	public void exitGame();
	public void undo();
	public boolean isBuilt();
	public void setVisibility(boolean visibility);
	public String newGameRequest();
	public void inGameResign();
	public void resignGame();	//Goes to main menu rather than exit game.

	public void keyPressed(KeyEvent e);
}
