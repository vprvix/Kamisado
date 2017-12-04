package Exceptions;

@SuppressWarnings("serial")
public class NoPossibleMovesException extends Exception {
	public NoPossibleMovesException(String message){
		super(message);
	}
}