package Network;

import Data.Move;
import Modelling.BoardModel;

public interface Network_Server {
	public Move getMove();
	public void send(BoardModel model);
	public void close();
	public String getUser();
	public String getIP();
	public boolean isClosed();
}
