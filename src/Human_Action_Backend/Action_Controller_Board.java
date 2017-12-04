package Human_Action_Backend;

import java.util.List;

import Players.Player;

public interface Action_Controller_Board {
	   public void RoundWin(Player player);
	   public void GameWin();
	   public void RoundDraw();
	   public void AlertUser(String str);
}
