package Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import Data.Move;
import GUIs.GameUI;
import Modelling.BoardModel;

public class Server implements Network_Server {
	private final int PORT = 5463;
	public ServerSocket server;
	public boolean availible;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Socket Client;
	private String remoteUser;

	public Server(String username) {
	
		handShake(username);
	}

	public void send(BoardModel model) {
		if (!Client.isClosed()) {
			GameUI gui = model.getGameUI();
			model.setGameUI(null);
			try {
				out.reset();
				out.writeObject(model);
			} catch (IOException e) {
				close();
				display("Ahh dang i could not write the model");
			}
			model.setGameUI(gui);
		}
	}

	@Override
	public void close() {
		availible = false;
		if (Client != null)
			if (!Client.isClosed())
				try {
					Client.close();
					display("Client Disconnected");
				} catch (IOException e) {
					display("I failed to close the socket for some reason");
				}
	}

	private void handShake(String username) {

		try {
			server = new ServerSocket(PORT);
			display(InetAddress.getLocalHost() + ", Continue to host");
			Client = server.accept();
			in = new ObjectInputStream(Client.getInputStream());
			out = new ObjectOutputStream(Client.getOutputStream());
			out.writeObject(new String("vPr546378" + "," + username));
			String[] incoming = ((String) in.readObject()).split(",");
			availible = (incoming[0].equals("MkVIIIA27M"));
			if (availible) {
				remoteUser = incoming[1];
				display("Connecred");
			}
		} catch (IOException e) {
			display("The client has been impolite");
			availible = false;
			close();
		} catch (ClassNotFoundException e) {
			availible = false;
		}

	}

	@Override
	public String getIP() {
		if (availible) {
			try {
				return "IP: " + Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				return "-1";
			}
		}
		return "-1";
	}

	@Override
	public Move getMove() {
		try {
			return (Data.Move) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}

	@Override
	public String getUser() {
		return remoteUser;
	}

	private void display(String str) {
		JOptionPane.showMessageDialog(null, str);

	}

	@Override
	public boolean isClosed() {
		if (server != null)
			return server.isClosed();
		else
			return false;
	}

}
