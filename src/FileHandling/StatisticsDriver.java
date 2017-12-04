package FileHandling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Statistics_Backend.Statistics_Player;

public class StatisticsDriver implements StatisticsIOInterface {
	private List<Statistics_Player> Players;
	private FileSupport fs;

	// load the wins and loses from memory
	public StatisticsDriver() {
		fs = new FileSupport("Scores");
		Players = new ArrayList<Statistics_Player>();
		for (File file : fs.getListing()) {
			if (file.getName().endsWith(".src"))
				Players.add((Statistics_Player) fs.loadFromFile(file.getName()));
		}
	}

	// returns number of losses against a player
	public int getCurrentWins(String name1, String name2) {
		for (int i = 0; i < Players.size(); i++) {
			if (Players.get(i).getUsername().equals(name1))
				return Players.get(i).getWins(name2);
		}
		return 0;
	}

	// returns number of losses against a player
	public int getCurrentLosses(String name1, String name2) {
		for (int i = 0; i < Players.size(); i++) {
			if (Players.get(i).getUsername().equals(name1))
				return Players.get(i).getLoses(name2);
		}
		return 0;
	}

	@Override
	public ArrayList<String> getAllUsers() {
		ArrayList<String> returnable = new ArrayList<String>();
		for (Statistics_Player current : Players) {
			returnable.add(current.getUsername());
		}
		return returnable;

	}

	@Override
	public void saveUserWin(String winner, String enemy) {
		Boolean Winfound = false;
		Boolean Lossfound = false;
		if (Players.size() > 0)
			for (Statistics_Player x : Players) {
				if (x.getUsername().equals(winner)) {
					x.addWin(enemy);
					Winfound = true;
				}
				if (x.getUsername().equals(enemy)) {
					x.addLoss(winner);
					Lossfound = true;
				}
				fs.saveToFile(x.getUsername() + ".src", x);
			}
		if (!Winfound) {
			Statistics_Player win = new Statistics_Player(winner);
			win.addWin(enemy);
			fs.saveToFile(win.getUsername() + ".src", win);
		}
		if (!Lossfound) {
			Statistics_Player loss = new Statistics_Player(enemy);
			loss.addLoss(enemy);
			fs.saveToFile(loss.getUsername() + ".src", loss);
		}

	}

}
