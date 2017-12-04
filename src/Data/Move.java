package Data;

public class Move implements java.io.Serializable{
	private int x1, x2, y1, y2;
	private boolean isFirstPlayer;
	
	public Move() {
		x1 = -1;
		x2 = -1;
		y1 = -1;
		y2 = -1;
		isFirstPlayer = false;
	}
	public void swap(){
		int temp1 = x1;
		int temp2 = y1;
		x1 = x2;
		y1 = y2;
		x2 = temp1;
		y2 = temp2;
	}
	
	public boolean isFirstPlayer(){
		return isFirstPlayer;
	}
	public void setPlayer(boolean isFirstPlayer){
		this.isFirstPlayer=isFirstPlayer;
	}
	public Move(int x1, int y1, int x2, int y2, boolean isFirstPlayer){
		this.isFirstPlayer = isFirstPlayer;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public Move duplicate(){
		return new Move(x1,y1,x2,y2, isFirstPlayer);
	}
	public void setOrigin(int x, int y) {
		x1 = x;
		y1 = y;
	}

	public int[] getOrigin() {
		int[] coord = { x1, y1 };
		return coord;
	}

	public void setTarget(int x, int y) {
		x2 = x;
		y2 = y;
	}

	public int[] getTarget() {
		int[] coord = { x2, y2 };
		return coord;
	}
	public void resetOrigin(){
		x1 = -1;
		y1 = -1;
	}
	public void resetTarget(){
		x2 = -1;
		y2 = -1;
	}
	public int[] getMove() {
		int[] Moves = { x1, x2, y1, y2 };
		return Moves;
	}
	public boolean isSamePlace(){
		return (x1 == x2 && y1 == y2);
	}
	@Override
	public String toString(){
		return "(" + x1 + "," + y1 +")=>"+ "(" + x2 + "," + y2 +")";
	}
	
	
	public boolean equals(Move m){
		return (x1==m.getOrigin()[0]&&y1==m.getOrigin()[1] && m.getTarget()[0]==x2 && m.getTarget()[1]==y2);
	}
	public void setOrigin(int[] origin) {
		setOrigin(origin[0], origin[1]);
		
	}
}