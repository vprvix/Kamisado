package Statistics_Backend;

import java.util.List;

import FileHandling.StatisticsDriver;
import FileHandling.StatisticsIOInterface;
import GUIs.StatisticsView;

public class StatisticsController implements StatisticsControllerInterface {
	private StatisticsIOInterface driver;
	
	public StatisticsController(StatisticsView x){
		driver = new StatisticsDriver();
	}
	public int getWins(String name1, String name2) {
		return driver.getCurrentWins(name1, name2);
	}

	public int getLosses(String name1, String name2) {
		return driver.getCurrentLosses(name1, name2);
	}

	@Override
	public int getTotalLoses(String name) {
		int returnable = 0;
		for(String x : driver.getAllUsers())
			returnable += driver.getCurrentLosses(name, x);
		return returnable;
	}

	@Override
	public int getTotalWins(String name) {
		int returnable = 0;
		for(String x : driver.getAllUsers())
			returnable += driver.getCurrentWins(name, x);
		return returnable;
	}
	@Override
	public List<String> getPlayers() {
		return driver.getAllUsers();
	}
	
	public static void SaveUser(String winner, String loser){
		StatisticsIOInterface drive = new StatisticsDriver();
		drive.saveUserWin(winner, loser);
	}


}
