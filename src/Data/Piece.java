package Data;
import java.awt.Color;

import Players.Player;

public class Piece implements java.io.Serializable{
 
   
   private Color colour;
   private Player owner;
   private int sumo, value, tempSumo, distance;
   
   public Piece(){
	  this.value = 1;
	  this.sumo = 0;
	  this.distance = 7;
   }
   
   public Piece(int sumo, int value, int distance){
	   this.sumo = sumo;
	   tempSumo=sumo;
	   this.value=value;
	   this.distance=distance;
   }
   
   private Piece(int sumo, int value, int tempSumo, int distance){
	   this.sumo=sumo;
	   this.tempSumo=tempSumo;
	   this.value=value;
	   this.distance=distance;
   }
   public void setColour(Color value) {
      this.colour = value;
   }
   
   public Color getColour() {
      return this.colour;
   }
   
   public void sumoUp(){
	   value=value*2 +1;
	   sumo=sumo+2;
	   tempSumo=sumo;
	   distance=distance-2;
   }
   public void moveSumo(){
	   tempSumo--;
   }
   public void resetSumo(){
	   tempSumo=sumo;
   }
   public int getDistance(){
	   return distance;
   }
   public int getValue(){
	   return value;
   }
   public boolean canSumo(int numberOfPieces){
	   return sumo>=numberOfPieces;
   }
   public void setOwner(Player value) {
      this.owner = value;
   }
   public int getSumoIndex(){
	   return (sumo/2);
   }
   
   public Player getOwner() {
      return owner;
   }
   
   public Piece duplicate(){
	   Piece piece = new Piece(sumo,value, tempSumo, distance);
	   piece.setColour(this.colour);
	   piece.setOwner(this.owner);
	   
	   return piece;
   }
   
   
   public String toString(){
	   return "^-^";
   }
   
   }
