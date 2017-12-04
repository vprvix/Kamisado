package Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Data.Move;
import Modelling.BoardModel;

public class Client implements Network_Client, java.io.Serializable {
	private final int PORT = 5463;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String remoteUsername;

	public boolean connect(String address, String username) {
		boolean attempt = true;
		while (attempt) {
			try {
				socket = new Socket(address, PORT);
				if (socket.isConnected()) {
					out = new ObjectOutputStream(socket.getOutputStream());
					in = new ObjectInputStream(socket.getInputStream());
					handShake(username);
					return true;
				}
			} catch (UnknownHostException e) {
				display("The host was unreachable");
				attempt = false;
			} catch (IOException e) {
				display("Could not reach host at " + address + " .");
				attempt = JOptionPane.showConfirmDialog(new JFrame(), "Continue Connecting?", "Kamisado",
						JOptionPane.YES_NO_OPTION) == 0;
			} catch (ClassNotFoundException e) {
				// TODO Mess with
				e.printStackTrace();
			}

		}
		close();
		return false;
	}

	private boolean handShake(String userName) throws IOException, ClassNotFoundException {
		String[] incoming = ((String) in.readObject()).split(",");
		if (incoming[0].equals("vPr546378")) {
			remoteUsername = incoming[1];
			out.writeObject(new String("MkVIIIA27M," + userName));
			display("Connected");
			return true;
		} else {
			return false;
		}
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
				display("Close");
			} catch (IOException e) {
				display("Could not close the socket");
			}
		}
	}

	public void send(Move move) {
		if (socket != null) {
			try {
				out.writeObject(move);
			} catch (IOException e) {
				display("There has been a fatal error in communication with the server");
				close();
			}
		}
	}

	public BoardModel getModel() {
		if (socket != null) {
			try {
				try {
					BoardModel model = (BoardModel) in.readObject();
					return model;
				} catch (IOException e) {
					display("This is a tragedy : " + e.getMessage());
					close();
				}
			} catch (ClassNotFoundException e) {
				display("Ugh ohh i have ran into a error");
				close();
			}
		}
		return null;

	}

	private void display(String str) {
		JOptionPane.showMessageDialog(null, str);

	}

	@Override
	public String getUsername() {
		return remoteUsername;
	}

	@Override
	public boolean isClosed() {
		if(socket == null) return true;
		return socket.isClosed();
	}

}
