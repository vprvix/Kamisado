package Human_Action_Backend;

import java.util.List;

import GUIs.GameUI;

public interface Action_Controller_GUI {
	public boolean buttonBoardClick(String buttonName) ;  
	   public ActionController saveButtonClick();
	   public boolean loadButtonClick(GameUI gameUI, String FileADDR);
	   public boolean undoButtonClick();
	   public boolean exitButtonClick();
	   public List<String> loadStart();
	   public void resetUI(GameUI gameUI);
	   public void resign();
	   public void inGameResign();
}
