package PieceMaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;

import Data.BoardColours;

public class PieceMakerGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel display;
	private JLabel display2;
	private BufferedImage currentImage;
	private final int RED = -65536;
	private final int GREEN = -16711936;
	private JProgressBar loading;
	private JComboBox<String> choice;
	private JSlider tolerance;

	public static void main(String[] args) {
		new PieceMakerGUI();

	}

	public PieceMakerGUI() {
		buildBar();
		Build();
		try {
			currentImage = getImageIO(getFile());
			upDateImage();
		} catch (IOException e) {
			
		}
	}

	private int[] getImageSize() {
		int[] x = { currentImage.getWidth(), currentImage.getHeight() };
		return x;
	}

	private void upDateImage() {
		ImageIcon current = new ImageIcon(currentImage);
		display.setIcon(current);
		ImageIcon current2 = new ImageIcon(BoardColours.convertImage(currentImage, 7)[1]);
		display2.setIcon(current2);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private File getFile() {
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return null;
		File file = fileChooser.getSelectedFile();
		return file;
	}

	private BufferedImage getImageIO(File file) throws IOException {
		if (file == null)
			throw new IOException("The file is invalid");
		BufferedImage image = ImageIO.read(file);
		return image;
	}

	protected void save() {
		try {
			scaleDown();
			ImageIO.write(currentImage, "png", new File("src/X.png"));
		} catch (IOException exc) {
			return;
		}
	}

	protected void Load() {
		try {
			currentImage = getImageIO(getFile());
			upDateImage();
		} catch (IOException e) {
			System.out.println("File not found");
		}
	}

	private void scaleDown() {
		int[] size = getImageSize();
		while (size[0] > 128 && size[1] > 128) {
			BufferedImage bfi = new BufferedImage(size[0] / 2, size[1] / 2, BufferedImage.TYPE_4BYTE_ABGR);

			for (int x = 0; x < size[0] / 2; x++) {
				for (int y = 0; y < size[1] / 2; y++) {
					Color[] sample = { new Color(currentImage.getRGB(x * 2, y * 2)),
							new Color(currentImage.getRGB(x * 2 + 1, y * 2)),
							new Color(currentImage.getRGB(x * 2, y * 2 + 1)),
							new Color(currentImage.getRGB(x * 2 + 1, y * 2 + 1)) };
					int redCount = 0;
					int greenCount = 0;
					for (int i = 0; i < 4; i++) {
						if (sample[i].getRGB() == RED) {
							redCount++;
						} else if (sample[i].getRGB() == GREEN) {
							greenCount++;
						}
					}
					if (redCount > greenCount) {
						bfi.setRGB(x, y, RED);
					} else if (greenCount > redCount) {
						bfi.setRGB(x, y, GREEN);
					} else if (redCount == 0) {

					} else {
						bfi.setRGB(x, y, RED);
					}
				}
			}
			currentImage = bfi;
			size = getImageSize();
		}
		upDateImage();

	}

	protected void Swap() {
		if (currentImage != null) {
			int[] size = getImageSize();
			loading.setValue(0);
			loading.setMaximum(size[0] * size[1]);
			SetColours(new Color(GREEN), new Color(RED));
		}
	}

	private void SetColours(Color color1, Color color2) {
		int[] size = getImageSize();
		BufferedImage bfi = new BufferedImage(size[0], size[1], BufferedImage.TYPE_4BYTE_ABGR);
		for (int x = 0; x < size[0]; x++) {
			for (int y = 0; y < size[1]; y++) {
				if (currentImage.getRGB(x, y) == color1.getRGB()) {
					bfi.setRGB(x, y, RED);
				} else if (currentImage.getRGB(x, y) == color2.getRGB()) {
					bfi.setRGB(x, y, GREEN);
				}
			}
		}
		currentImage = bfi;
		upDateImage();
	}

	protected void mouseClick(int x, int y) {
		if (currentImage != null) {

			ColourChanger(currentImage.getRGB(x, y));
		}
	}

	private void ColourChanger(int swappable) {
		int[] size = getImageSize();
		int changeTo = new Color(0,0,0,0).getRGB();
		Color toChange = new Color(swappable);
		int tol = tolerance.getValue();
		loading.setValue(0);
		loading.setMaximum(size[0] * size[1]);
		if (choice.getSelectedItem() == "Player") {
			changeTo = RED;
		} else if (choice.getSelectedItem() == "Colour"){
			changeTo = GREEN;
		}
		for (int x = 0; x < size[0]; x++) {
			for (int y = 0; y < size[1]; y++) {
				loading.setValue(loading.getValue() + 1);
				Color sample = new Color(currentImage.getRGB(x, y));
				if (tolerance(toChange.getRed(), sample.getRed(), tol)
						&& tolerance(toChange.getGreen(), sample.getGreen(), tol)
						&& tolerance(toChange.getBlue(), sample.getBlue(), tol)) {
					currentImage.setRGB(x, y, changeTo);
				}
			}
		}
		upDateImage();
	}
	
	

	private boolean tolerance(int x, int y, int tol) {
		int out = x - y;
		if (y > x)
			out = y - x;
		if (out < tol && out > -1 * tol)
			return true;
		return false;
	}
	// ----------------------GUI

	private void buildBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu options = new JMenu("File");
		JMenu operations = new JMenu("Tools");
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		menuBar.add(options);
		menuBar.add(operations);

		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		options.add(save);

		JMenuItem load = new JMenuItem("Load Image");
		load.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Load();
			}
		});
		options.add(load);

		JMenuItem swap = new JMenuItem("Swap colours");
		swap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Swap();
			}
		});
		operations.add(swap);


		JMenuItem ScaleDown = new JMenuItem("Scale down");
		ScaleDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread x = new Thread(new Runnable() {
					public void run() {
						scaleDown();
					}
				});
				x.start();

			}
		});
		operations.add(ScaleDown);

		this.setJMenuBar(menuBar);
	}

	private void Build() {
		this.setTitle("Dev Icon Builder");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JPanel center = new JPanel();
		JPanel bottom = new JPanel();
		loading = new JProgressBar();
		display = new JLabel();
		display2 = new JLabel();
		display.setBorder(BorderFactory.createLoweredBevelBorder());
		display2.setBorder(BorderFactory.createLoweredBevelBorder());
		display.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				mouseClick(e.getX(), e.getY());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		choice = new JComboBox<String>();

		tolerance = new JSlider();
		tolerance.setMaximum(128);
		tolerance.setMinimum(1);
		tolerance.setValue(10);
		tolerance.setMajorTickSpacing(16);
		choice.addItem("Player");
		choice.addItem("Colour");
		choice.addItem("Clear");
		center.add(display);
		center.add(display2);
		bottom.add(new JLabel("Progress"));
		bottom.add(loading);
		bottom.add(new JLabel("Celection Type"));
		bottom.add(choice);
		bottom.add(new JLabel("Tolerance"));
		bottom.add(tolerance);
		display.validate();
		this.add(center, BorderLayout.CENTER);
		this.add(bottom, BorderLayout.SOUTH);
		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

}
