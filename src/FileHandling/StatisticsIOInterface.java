package FileHandling;

import java.util.ArrayList;

public interface StatisticsIOInterface {
	public abstract int getCurrentWins(String name1, String name2);
	public abstract int getCurrentLosses(String name1, String name2);
	public ArrayList<String> getAllUsers();
	public abstract void saveUserWin(String winner, String Loser);
}
