package GUIs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Statistics_Backend.StatisticsController;
import Statistics_Backend.StatisticsControllerInterface;

public class StatisticsView extends JFrame implements StatisticsUI {
	private JComboBox Player1;
	private JComboBox Player2;
	private JLabel Player1TotalWins;
	private JLabel Player2TotalWins;
	private JLabel Player1TotalLoss;
	private JLabel Player2TotalLoss;
	private JLabel MainScreen;
	private StatisticsControllerInterface statController;

	public StatisticsView() {
		statController = new StatisticsController(this);
		build();
	}

	@Override
	public void getAndDisplayWins() {
	}

	@Override
	public void getAndDisplayLosses() {

	}

	@Override
	public void build() {
		this.setTitle("Statistics");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());

		List<String> players = statController.getPlayers();

		JPanel Left = new JPanel();
		JPanel Right = new JPanel();
		JPanel Center = new JPanel();
		JPanel TOP = new JPanel();
		Player1 = new JComboBox();
		Player2 = new JComboBox();
		Player1TotalWins = new JLabel("0");
		Player2TotalWins = new JLabel("0");
		Player1TotalLoss = new JLabel("0");
		Player2TotalLoss = new JLabel("0");
		MainScreen = new JLabel(" 0 - 0 ");
		Left.add(new JLabel("Player"));
		Right.add(new JLabel("Player"));
		for (String player : players) {
			Player1.addItem(player);
			Player2.addItem(player);
		}
		Player1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setText();
			}
		});
		Player2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setText();
			}
		});

		Left.add(Player1);
		Right.add(Player2);
		Left.add(new JLabel("Total Wins: "));
		Left.add(Player1TotalWins);
		Right.add(new JLabel("Total Wins: "));
		Right.add(Player2TotalWins);
		Left.add(new JLabel("Total Loss: "));
		Left.add(Player1TotalLoss);
		Right.add(new JLabel("Total Loss: "));
		Right.add(Player2TotalLoss);
		TOP.add(MainScreen);

		Center.add(Left);
		Center.add(Right);
		this.add(Center, BorderLayout.CENTER);
		this.add(TOP, BorderLayout.NORTH);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		setText();

	}

	private void setText() {
		String player = (String) Player1.getSelectedItem();
		String player2 = (String) Player2.getSelectedItem();
		Player1TotalWins.setText("" + statController.getTotalWins(player));
		Player1TotalLoss.setText("" + statController.getTotalLoses(player));
		Player2TotalWins.setText("" + statController.getTotalWins(player2));
		Player2TotalLoss.setText("" + statController.getTotalLoses(player2));
		MainScreen.setText(player + " | " + statController.getWins(player, player2) + "  :  "
				+ statController.getLosses(player, player2) + " | " + player2);

	}
}
