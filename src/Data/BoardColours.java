package Data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class BoardColours implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2021686319004156996L;
	public final static Color default_orange = new Color(0xff, 0x66, 0x00);
	public final static Color default_blue = new Color(0, 153, 255);
	public final static Color default_purple = new Color(153, 52, 255);
	public final static Color default_pink = new Color(255, 102, 163);
	public final static Color default_yellow = new Color(255, 204, 0);
	public final static Color default_red = Color.red;
	public final static Color default_green = new Color(0, 204, 0);
	public final static Color default_brown = new Color(160, 82, 45);
	private final static int PLAYER = -65536;
	private final static int TEAM = -16711936;
	public final static Color[] DEFAULT_COLOUR_ORDER = { default_orange, default_blue, default_purple, default_pink,
			default_yellow, default_red, default_green, default_brown };

	public Color orange = default_orange;
	public Color blue = default_blue;
	public Color purple = default_purple;
	public Color pink = default_pink;
	public Color yellow = default_yellow;
	public Color red = default_red;
	public Color green = default_green;
	public Color brown = default_brown;

	public Color[] COLOUR_ORDER = { orange, blue, purple, pink, yellow, red, green, brown };

	private Color[][] board;

	public BoardColours() {
		assignBoard();
	}

	public static Color[] defaultOrder() {
		return DEFAULT_COLOUR_ORDER;
	}

	private Color[][] assignBoard() {

		board = new Color[8][8];
		for (int i = 0; i < 4; i++) {
			board[i][i] = orange;
			board[7 - i][i] = brown;
			board[3 - i][i] = pink;
			board[4 + i][i] = yellow;

		}
		board[1][0] = blue;
		board[2][3] = blue;
		board[4][1] = blue;
		board[7][2] = blue;

		board[1][3] = purple;
		board[2][0] = purple;
		board[4][2] = purple;
		board[7][1] = purple;

		board[0][1] = red;
		board[3][2] = red;
		board[5][0] = red;
		board[6][3] = red;

		board[0][2] = green;
		board[3][1] = green;
		board[5][3] = green;
		board[6][0] = green;

		for (int k = 0; k < 8; k++) {
			for (int j = 4; j < 8; j++) {
				board[k][j] = board[7 - k][7 - j];
			}
		}
		return board;
	}

	public Color getColor(int x, int y) {
		if (board[x][y] != null) {
			return board[x][y];
		}
		return Color.black;
	}

	public static BufferedImage[] convertImage(BufferedImage refernce, int place) {
		BufferedImage[] returnable = new BufferedImage[2];
		returnable[0] = new BufferedImage(refernce.getWidth(), refernce.getHeight(), BufferedImage.TYPE_INT_ARGB);
		returnable[1] = new BufferedImage(refernce.getWidth(), refernce.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < refernce.getHeight(); y++) {
			for (int x = 0; x < refernce.getWidth(); x++) {
				if (refernce.getRGB(x, y) == PLAYER) {
					returnable[0].setRGB(x, y, Color.WHITE.getRGB());
					returnable[1].setRGB(x, y, Color.BLACK.getRGB());
				} else if (refernce.getRGB(x, y) == TEAM) {
					returnable[0].setRGB(x, y, DEFAULT_COLOUR_ORDER[7 - place].getRGB());
					returnable[1].setRGB(x, y, DEFAULT_COLOUR_ORDER[place].getRGB());
				}
			}
		}
		return returnable;
	}

	public static Icon combine(Icon target, Icon convert) {
		BufferedImage image1 = new BufferedImage(target.getIconWidth(), target.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage image2 = new BufferedImage(convert.getIconWidth(), convert.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g1 = image1.createGraphics();
		Graphics g2 = image1.createGraphics();
		target.paintIcon(null, g1, 0, 0);
		convert.paintIcon(null, g2, 0, 0);
		int height = image1.getHeight();
		if (image1.getHeight() < image2.getHeight()) {
			height = image2.getHeight();
		}

		int width = image1.getWidth();
		if (image1.getWidth() < image2.getWidth()) {
			width = image2.getWidth();
		}
		BufferedImage imageNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// add image 1
		int Xindent = (width - image1.getWidth()) / 2;
		int Yindent = (height - image1.getHeight()) / 2;
		for (int x = 0; x < image1.getWidth(); x++) {
			for (int y = 0; y < image1.getHeight(); y++) {
				if (image1.getRGB(x, y) != 0)
					imageNew.setRGB(Xindent + x, Yindent + y, image1.getRGB(x, y));
			}
		}

		// add image 2
		Xindent = (width - image2.getWidth()) / 2;
		Yindent = (height - image2.getHeight()) / 2;
		for (int x = 0; x < image2.getWidth(); x++) {
			for (int y = 0; y < image2.getHeight(); y++) {
				if (image2.getRGB(x, y) != 0)
					imageNew.setRGB(Xindent + x, Yindent + y, image2.getRGB(x, y));
			}
		}
		return new ImageIcon(imageNew);
	}

	private static final int sumoVal = -16777216;

	public static ImageIcon createSumo(int times, BufferedImage standard, boolean isFirstPlayer) {
		int length = 128;
		int height = 128;
		int value = Color.BLACK.getRGB();
		if(!isFirstPlayer)
			value = Color.WHITE.getRGB();
		if (standard != null && standard.getWidth() == length && standard.getHeight() == height / 2) {
			BufferedImage returnable = new BufferedImage(length, height, BufferedImage.TYPE_INT_ARGB);

			// shape for the sumo

			for (int x = 0; x < length; x++) {
				for (int y = 0; y < height / 2; y++) {
					if (standard.getRGB(x, y) == sumoVal)
						returnable.setRGB(x, y, value);
				}
			}

			if (times > 1) {
				for (int x = length - 1; x > -1; x--) {
					for (int y = height/2; y < height-1 ; y++) {
						if (standard.getRGB(x, y - height/2) == sumoVal)
							returnable.setRGB(x, y , value);
					}
				}
				if (times > 2) {
					for (int x = length/2 - 1; x > -1; x--) {
						for (int y = 0; y < height; y++) {
							if (standard.getRGB(y, x) == sumoVal)
								returnable.setRGB(x + length/2, y, value);
						}
					}
					for (int x = length/2 - 1; x > -1; x--) {
						for (int y = 0; y < height; y++) {
							if (standard.getRGB(y, x) == sumoVal)
								returnable.setRGB(x, y, value);
						}
					}
				}
			}
			ImageIcon newIcon = new ImageIcon(returnable);
			return newIcon;
		}
		return null;
	}

	public void shift() {
		Collections.shuffle(Arrays.asList(COLOUR_ORDER));
		assign(COLOUR_ORDER);
		assignBoard();

	}

	public Color[] assign(Color... n) {
		if (n.length != 8)
			return COLOUR_ORDER;
		orange = n[0];
		blue = n[1];
		purple = n[2];
		pink = n[3];
		yellow = n[4];
		red = n[5];
		green = n[6];
		brown = n[7];

		return COLOUR_ORDER;
	}

}
