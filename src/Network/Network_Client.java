
package Network;

import Data.Move;
import Modelling.BoardModel;


public interface Network_Client {
	public void send(Move move);
	public void close();
	public String getUsername();
	public BoardModel getModel();
	public boolean connect(String address, String username);
	public boolean isClosed();
}
