package Statistics_Backend;

import java.util.HashMap;

public class Statistics_Player implements java.io.Serializable {
	private static final long serialVersionUID = -1091851945809508370L;
	String username;
	HashMap<String, Integer> Wins;
	HashMap<String, Integer> Loses;

	public Statistics_Player(String username) {
		this.username = username;
		Wins = new HashMap<String, Integer>();
		Loses = new HashMap<String, Integer>();
	}

	public int getWins(String opp) {
		if (Wins.containsKey(opp)) {
			return Wins.get(opp);
		}
		return 0;
	}

	public void addWin(String enemy) {
		if (Wins.containsKey(enemy)) {
			Wins.replace(enemy, Wins.get(enemy) + 1);
		} else {
			Wins.put(enemy, 1);
		}
	}
	
	public void addLoss(String enemy) {
		if (Loses.containsKey(enemy)) {
			Loses.replace(enemy, Loses.get(enemy) + 1);
		} else {
			Loses.put(enemy, 1);
		}
	}

	public int getLoses(String opp) {
		if (Loses.containsKey(opp)) {
			return Loses.get(opp);
		}
		return 0;
	}

	public double getRatio(String opp) {

		int wins = 0;
		int loses = 0;
		if (Wins.containsKey(opp))
			wins += Wins.get(opp);
		if (Loses.containsKey(opp))
			loses += Loses.get(opp);
		double ratio = wins;
		if (loses != 0)
			ratio = wins / loses - 1;

		return ratio;
	}

	public String getUsername() {
		return username;
	}
}
