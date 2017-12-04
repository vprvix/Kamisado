package Statistics_Backend;

import java.util.List;

public interface StatisticsControllerInterface {
	/**
	 * <pre>
	 *           1..1     1..1
	 * StatisticsControllerInterface ------------------------- StatisticsDriver
	 *           statisticsControllerInterface        &gt;       statisticsDriver
	 * </pre>
	 */

	public int getWins(String name1, String name2);

	public int getLosses(String name1, String name2);

	public int getTotalLoses(String name1);
	public int getTotalWins(String name2);
	public List<String> getPlayers();
}
