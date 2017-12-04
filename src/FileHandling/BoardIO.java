package FileHandling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Human_Action_Backend.ActionController;

public class BoardIO {
	// Save the current state of the board
	public static boolean saveGame(ActionController ACI, String filename) {
		FileSupport fileSupport = new FileSupport("Save");
		fileSupport.saveToFile(filename.replace(" ", "").replace(":", ""), ACI);
		return true;
	}

	// Retries valid files to give to the user interface
	public static List<String> fileList() {
		List<String> returnable = new ArrayList<>();
		List<File> files = new FileSupport("Save").getListing();
		for (File file : files) {
			if (file.getName().endsWith(".sav")) {
				returnable.add(file.getName());
			}
		}
		return returnable;
	}

	public static ActionController loadGame(String filename) {
		FileSupport fileSupport = new FileSupport("Save");
		ActionController file = (ActionController)fileSupport.loadFromFile(filename);
		return file;
	}

}
