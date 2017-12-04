package GUIs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Data.BoardColours;
import Data.Move;
import Data.Piece;
import Exceptions.NoPossibleMovesException;
import FileHandling.BoardIO;
import Human_Action_Backend.ActionControllerLocalSingle;
import Human_Action_Backend.ActionControllerLocalTwo;
import Human_Action_Backend.Action_ControllerNetwork;
import Human_Action_Backend.Action_Controller_GUI;
import Human_Action_Backend.Action_Controller_Host;
import Modelling.BoardModel;
import Network.Network_Server;
import Network.Server;
import Players.Ai;
import Players.Human;
import Players.Player_Client;

public class GameView extends JPanel implements GameUI, Game_Panel_Interface {
	private JButton[][] board;
	private Action_Controller_GUI controller;
	private JLabel turnLabel;
	private int[] selected;
	private boolean Built;
	private BufferedImage defaultIcon;
	private Icon Possible;
	private BufferedImage sumoIcon;
	private Icon[] player1Icons;
	private Icon[] player2Icons;
	private GameLoader Master;
	private JLabel Time_Display;
	private BoardColours colours;

	public GameView(GameLoader Master) {
		this.Master = Master;
		Built = false;
		this.colours = new BoardColours();
		selected = new int[2];
		selected[0] = 7;
		selected[1] = 7;
		try {
			defaultIcon = ImageIO.read(new File("src\\x.png"));
		} catch (IOException e) {
			this.alertUser("ERROR: Image missing, place at src\\x.png");
			System.exit(0);
		}
		buildButtonFrame();
		initialiseIcons();

	}

	@Override
	public void build() {
		Built = true;
		this.setLayout(new BorderLayout());
		JPanel boardSection = new JPanel();
		board = new JButton[8][8];

		GridLayout layout = new GridLayout(8, 8);
		boardSection.setLayout(layout);
		BoardColours bc = new BoardColours();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				String s = "" + j + "," + i;
				board[j][i] = new JButton();
				board[j][i].setBackground(colours.getColor(j, i));
				board[j][i].setActionCommand(s);
				board[j][i].setFocusable(false);
				// board[j][i].setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED,
				// Color.BLACK, Color.BLACK));
				board[j][i].setBorder(BorderFactory.createStrokeBorder(new BasicStroke(1.5f)));
				board[j][i].addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (controller != null)
							controller.buttonBoardClick(arg0.getActionCommand());
						else
							alertUser("I cant let you do that dave");
					}

				});
				boardSection.add(board[j][i]);
			}
		}
		initialisePieces();
		JPanel InfoPanel = new JPanel();
		turnLabel = new JLabel("Pick the first piece, white goes first.");
		Time_Display = new JLabel("X");
		InfoPanel.add(turnLabel);
		InfoPanel.add(Time_Display);
		this.add(InfoPanel, BorderLayout.PAGE_END);
		this.add(boardSection, BorderLayout.CENTER);
		board[selected[0]][selected[1]]
				.setBackground(board[selected[0]][selected[1]].getBackground().darker().darker());
		this.repaint();

	}

	@Override
	public void updateUI(BoardModel boardModel) {
		if (boardModel != null) {
			colours = boardModel.colourHolder;
			ArrayList<Move> possible = boardModel.getAvailibleMoves();
			if (boardModel.isFirstTurn()){
				possible = new ArrayList<>();
				
				try {
					possible = boardModel.addAvailableMoves(boardModel.getOrigin()[0], boardModel.getOrigin()[1]);
				} catch (NoPossibleMovesException e) {
					//nah, not a thing here.
				}
			}
			int[] currentPiece = boardModel.getOrigin();
			moveSelected(currentPiece[0], currentPiece[1]);
			Piece[][] modelBoard = boardModel.getBoardArray();
			BoardColours BC = boardModel.colourHolder;
			turnLabel.setText("White : " + boardModel.getfirstPlayer().getUsername() + " Score: "
					+ boardModel.getfirstPlayer().getScore() + "       Black : "
					+ boardModel.getsecondPlayer().getUsername() + " Score : "
					+ boardModel.getsecondPlayer().getScore());

			// loop through all the positions
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					board[x][y].setBackground(colours.getColor(x, y));
					// If there is a piece at that bit of the board:
					if (modelBoard[x][y] != null) {
						// and it belongs to the first player
						String usrNm = boardModel.getfirstPlayer().getUsername();
						if (modelBoard[x][y].getOwner().getUsername().equals(usrNm)) {
							for (int colorIndex = 0; colorIndex < 8; colorIndex++) {
								if (modelBoard[x][y].getColour().getRGB() == colours.DEFAULT_COLOUR_ORDER[colorIndex]
										.getRGB()) {
									board[x][y].setIcon(player1Icons[7 - colorIndex]);
									
								}
							}
							if(modelBoard[x][y].getSumoIndex() > 0){
								board[x][y].setIcon(BoardColours.combine(board[x][y].getIcon(),BoardColours.createSumo(modelBoard[x][y].getSumoIndex(), sumoIcon, true)));
							}
						} else {
							for (int colorIndex = 0; colorIndex < 8; colorIndex++) {
								if (modelBoard[x][y].getColour().getRGB() == colours.DEFAULT_COLOUR_ORDER[7 - colorIndex]
										.getRGB()) {
									board[x][y].setIcon(player2Icons[7 - colorIndex]);
								}
							}
							if(modelBoard[x][y].getSumoIndex() > 0){
								board[x][y].setIcon(BoardColours.combine(board[x][y].getIcon(),BoardColours.createSumo(modelBoard[x][y].getSumoIndex(), sumoIcon,false)));
							}
						}
						
					} else if (board[x][y].getIcon() != null) { // remove
																// artifact
																// images.
						board[x][y].setIcon(null);
					}

				}
			}

			for (Move m : possible) {
				int[] coord = m.getTarget();
				if (board[coord[0]][coord[1]].getIcon() != null) {
					board[coord[0]][coord[1]]
							.setIcon(BoardColours.combine( board[coord[0]][coord[1]].getIcon(), Possible));
				} else {
					board[coord[0]][coord[1]].setIcon(Possible);
				}
			}
			if (boardModel.getUndoList().size() > 0) {
				addLine(boardModel.getUndoList().get(boardModel.getUndoList().size() - 1));
			}
		}
	}

	public void addLine(Move m) {
		if (m.getTarget()[0] != -1 && m.getTarget()[1] != -1) {
			JButton origin = board[m.getOrigin()[0]][m.getOrigin()[1]];
			JButton target = board[m.getTarget()[0]][m.getTarget()[1]];

			this.getGraphics().drawLine(origin.getLocation().x + (int) origin.getSize().getWidth() / 2,
					origin.getLocation().y + (int) origin.getSize().getHeight() / 2,
					target.getLocation().x + (int) origin.getSize().getWidth() / 2,
					target.getLocation().y + (int) origin.getSize().getHeight() / 2);
		}
	}

	@Override
	public void displayVictory() {

	}

	@Override
	public void exitGame() {
		Built = false;
		controller.exitButtonClick();

	}

	@Override
	public void resignGame() {
		Built = false;
		Master.resign();
	}

	@Override
	public void undo() {
		if (controller != null) {
			if (controller.undoButtonClick()) {
				alertUser("Undone to limit");
			}
		}
	}

	private String onlineOptions() {
		final Object[] ROUND_OPTIONS = { 1, 3, 7, 15 };

		boolean Host = JOptionPane.showConfirmDialog(new JFrame(), "Hosting?", "Kamisado",
				JOptionPane.YES_NO_OPTION) == 0;
		String username1 = JOptionPane.showInputDialog("First player's name: ", "Player1");
		if (username1 == null) {
			Built = false;
			return null; // They cancelled
		}
		if (Host) {
			boolean isSpeedMode = JOptionPane.showConfirmDialog(new JFrame(),
					"Would you like to play in speed mode (30s per turn)?", "Kamisado", JOptionPane.YES_NO_OPTION) == 0;

			Object input = JOptionPane.showInputDialog(new JFrame(), "Select Number of rounds", "Kamisado",
					JOptionPane.PLAIN_MESSAGE, null, ROUND_OPTIONS, "");
			if (input == null) {
				Built = false;
				return null;
			}
			int rounds = (int) input;
			boolean firstPlayer = JOptionPane.showConfirmDialog(new JFrame(), username1 + " going first?", "Kamisado",
					JOptionPane.YES_NO_OPTION) == 0;
			GameView currentUI = this;
			Thread x = new Thread(new Runnable() {
				public void run() {
					Network_Server ser = new Server(username1);
					controller = new Action_Controller_Host(currentUI, new Human(username1),
							new Player_Client(ser.getIP() + ":" + ser.getUser()), isSpeedMode, rounds, ser,
							firstPlayer);
				}
			});
			x.start();
			return "Host";

		} else {
			GameView currentUI = this;
			Thread x = new Thread(new Runnable() {
				public void run() {
					controller = new Action_ControllerNetwork(currentUI, username1);
				}
			});
			x.start();
			return "Client";
		}
	}

	private String localOptions() {
		final Object[] ROUND_OPTIONS = { 1, 3, 7, 15 };
		final HashMap<String, Integer> conversion = new HashMap<>();

		final String[] AI_OPTIONS = { "Easy", "Medium", "Hard" };

		int i = 1;
		for (String n : AI_OPTIONS) {
			conversion.put(n, i);
			i += 1;
		}
		String username1 = JOptionPane.showInputDialog("First player's name: ", "Player1");
		if (username1 == null) {
			Built = false;
			return null; // They cancelled
		}
		boolean isSpeedMode = JOptionPane.showConfirmDialog(new JFrame(),
				"Would you like to play in speed mode (30s per turn)?", "Kamisado", JOptionPane.YES_NO_OPTION) == 0;

		Object input = JOptionPane.showInputDialog(new JFrame(), "Select Number of rounds", "Kamisado",
				JOptionPane.PLAIN_MESSAGE, null, ROUND_OPTIONS, "");
		if (input == null) {
			Built = false;
			return null;
		}
		int rounds = (int) input;
		boolean firstPlayer = JOptionPane.showConfirmDialog(new JFrame(), username1 + " going first?", "Kamisado",
				JOptionPane.YES_NO_OPTION) == 0;
		boolean twoPlayer = JOptionPane.showConfirmDialog(new JFrame(), "Playing with two people?", "Kamisado",
				JOptionPane.YES_NO_OPTION) == 0;

		if (twoPlayer) {
			String username2 = JOptionPane.showInputDialog("Second player's name:", "Player2");
			if (username2 == null) { // They cancelled
				Built = false;
				return null;
			}

			controller = new ActionControllerLocalTwo(this, new Human(username1), new Human(username2), isSpeedMode,
					rounds);
			return "Local Play";
		} else {
			String test = (String) JOptionPane.showInputDialog(new JFrame(), "Select AI Difficulty", "Kamisado",
					JOptionPane.PLAIN_MESSAGE, null, AI_OPTIONS, "");
			int aiDifficulty = 0;
			if (test == null)
				return null;
			else
				aiDifficulty = conversion.get(test);
			String username2 = "Rem";
			controller = new ActionControllerLocalSingle(this, new Human(username1), new Ai(username2, aiDifficulty),
					isSpeedMode, rounds, firstPlayer);

			return "Single Player";
		}
	}

	private String options() {

		boolean netPlay = JOptionPane.showConfirmDialog(new JFrame(), "Playing online?", "Kamisado",
				JOptionPane.YES_NO_OPTION) == 0;

		if (netPlay) {
			return onlineOptions();
		} else {
			return localOptions();
		}

		// should be no issues but ai input being 0 could cause issues?

	}

	// receives a keypress and sorts it into helpful categories
	public void keyPressed(KeyEvent e) {

		if (Built) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (selected[0] != 0) {
					moveSelected(selected[0] - 1, selected[1]);
				}
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (selected[0] != 7) {
					moveSelected(selected[0] + 1, selected[1]);
				}
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				if (selected[1] != 0) {
					moveSelected(selected[0], selected[1] - 1);
				}
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (selected[1] != 7) {
					moveSelected(selected[0], selected[1] + 1);
				}
			} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				controller.buttonBoardClick("" + selected[0] + "," + selected[1]);
			}

		}
	}

	// switch the location of selected to x,y
	private void moveSelected(int x, int y) {
		board[selected[0]][selected[1]].setBackground(board[selected[0]][selected[1]].getBackground().brighter());
		board[x][y].setBackground(board[x][y].getBackground().darker());
		selected[0] = x;
		selected[1] = y;
	}

	private void initialisePieces() {
		for (int i = 0; i < 8; i++) {
			board[i][7].setIcon(player1Icons[i]);
			board[i][0].setIcon(player2Icons[i]);
		}
	}

	private void initialiseIcons() {
		player1Icons = new Icon[8];
		player2Icons = new Icon[8];
		for (int i = 0; i < 8; i++) {
			BufferedImage[] images = BoardColours.convertImage(defaultIcon, i);
			player1Icons[i] = new ImageIcon(images[0]);
			player2Icons[i] = new ImageIcon(images[1]);
		}
		
		
	}

	private void buildButtonFrame() {
		try {
			Possible = new ImageIcon(ImageIO.read(new File("src\\frame.png")));
		} catch (IOException e) {
			this.alertUser("ERROR: Image missing, place at src\\frame.png");
			System.exit(0);
		}
		
		try {
			sumoIcon = ImageIO.read(new File("src\\sumo.png"));
		} catch (IOException e) {
			this.alertUser("ERROR: Image missing, place at src\\sumo.png");
			System.exit(0);
		}

	}

	@Override
	public void alertUser(String alertStr) {
		JOptionPane.showMessageDialog(null, alertStr);
	}

	@Override
	public boolean isBuilt() {
		return Built;
	}

	@Override
	public void setVisibility(boolean visibility) {
		this.setVisible(false);

	}

	@Override
	public void inGameResign() {
		if (controller != null)
			controller.inGameResign();

	}

	@Override
	public void saveRequest() {
		if (controller != null) {
			String FileID = JOptionPane.showInputDialog("Save name", "save" + new Date().toString());
			BoardIO.saveGame(controller.saveButtonClick(), FileID + ".sav");
			controller.resetUI(this);
		}
	}

	@Override
	public void loadRequest() {
		if (Built) {
			controller.resign();
		}
		this.build();
		Object[] saves = BoardIO.fileList().toArray();
		if (saves.length > 0) {
			String input = (String) JOptionPane.showInputDialog(new JFrame(), "Select save", "Kamisado",
					JOptionPane.PLAIN_MESSAGE, null, saves, "");
			controller = BoardIO.loadGame(input);
			if(controller == null){
				Built = false;
				return;
			}
			controller.resetUI(this);
		} else {
			alertUser("No saved games");
		}
	}

	@Override
	public String newGameRequest() {

		if (Built) {
			controller.resign();
		}
		build();
		String returnable = options();
		if (returnable == null)
			return null;

		return returnable;
	}

	@Override
	public void setClock(String str) {
		Time_Display.setText(str);

	}

}
