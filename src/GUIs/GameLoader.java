package GUIs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import PieceMaker.PieceMakerGUI;

public class GameLoader extends JFrame implements ViewUI, KeyListener {
	private JMenuBar menuBar;
	private Game_Panel_Interface gameView;
	private Container pane;
	private List<JMenuItem> importantButtons;
	private boolean running = false;
	private boolean resignPress = false;

	public static void main(String[] args) {
		new GameLoader();
	}

	public GameLoader() {
		build();
		addKeyListener(this);
	}

	private void setScreenSize() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void quit() {
		System.exit(0);
	}

	void resign() {

		if (gameView.isBuilt()) {
			if (resignPress) {
				resignPress = false;
				gameView.resignGame();
			} else {
				gameView.inGameResign();
			}
			for (JMenuItem optionDisable : importantButtons) {
				optionDisable.setEnabled(false);
			}
			gameView.setVisibility(false);
			setScreenSize();
			this.remove(this);
		}

	}

	protected void Undo() {
		gameView.undo();
	}

	private void newGame() {
		resign();
		build();
		String username = gameView.newGameRequest();
		if (username == null) {
			return;
		}
		this.setTitle(username);
		for (JMenuItem optionDisable : importantButtons) {
			optionDisable.setEnabled(true);
		}

		this.pack();
		setScreenSize();
	}

	protected void Load() {
		resign();
		build();

		gameView.loadRequest();

		for (JMenuItem optionDisable : importantButtons) {
			optionDisable.setEnabled(true);
		}
		this.pack();
		setScreenSize();

	}

	protected void Save() {
		gameView.saveRequest();
	}

	// -------GSP

	// TODO: Add correct statistics view initializer
	@Override
	public void build() {
		// used for getting keyboard shortcuts
		final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		importantButtons = new ArrayList<JMenuItem>();

		pane = new JPanel();
		gameView = new GameView(this);

		// Put gameview and statistics view side by side
		pane.setLayout(new BorderLayout());
		pane.add((Component) gameView, BorderLayout.CENTER);

		buildMenuBar(SHORTCUT_MASK);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setJMenuBar(menuBar);
		this.setContentPane(pane);
		this.pack();

		// add buttons to toggle list

		for (JMenuItem optionDisable : importantButtons) {
			optionDisable.setEnabled(false);
		}

		// make the screen appear in the center of the screen
		setScreenSize();
	}

	// -----------------MENUBAR

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (gameView.isBuilt())
			gameView.keyPressed(e);
	}

	private void buildMenuBar(int SHORTCUT_MASK) {
		menuBar = new JMenuBar();

		// Add menu bar with buttons and listeners. Listeners as external
		// methods here.
		JMenu menu = new JMenu("Menu");
		JMenu dataMenu = new JMenu("Data");
		JMenu extras = new JMenu("Extras");
		menuBar.add(menu);
		menuBar.add(dataMenu);
		menuBar.add(extras);
		menu.addSeparator();

		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		JMenuItem newGame = new JMenuItem("New Game");
		newGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				newGame();

			}

		});
		newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
		menu.add(newGame);

		JMenuItem resign = new JMenuItem("Resign");
		resign.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resignPress = true;
				resign();
			}

		});
		
		
		resign.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
		menu.add(resign);

		
		JMenuItem undo = new JMenuItem("Undo");
		undo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resignPress = true;
				Undo();
			}

		});
		
		
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_MASK));
		menu.add(undo);
		
		
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
		menu.add(quit);
		
		
		
		
		JMenuItem Save = new JMenuItem("Save");
		Save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Save();
			}
		});
		Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
		dataMenu.add(Save);
		
		JMenuItem Load = new JMenuItem("Load");
		Load.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Load();
			}
		});
		Load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
		dataMenu.add(Load);
		
		
		JMenuItem Statistics = new JMenuItem("Stats");
		Statistics.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread x = new Thread(new Runnable() {
					public void run() {
						new StatisticsView();
					}
				});
				x.start();
			}});
		extras.add(Statistics);
		
		
		JMenuItem piecemkr = new JMenuItem("Piece Maker");
		piecemkr.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread x = new Thread(new Runnable() {
					public void run() {
						new PieceMakerGUI();
					}
				});
				x.start();
			}});
		extras.add(piecemkr);
		importantButtons.add(resign);
		importantButtons.add(Save);
		importantButtons.add(undo);

	}
}