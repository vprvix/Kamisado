package FileHandling;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/*
 * Allows dumping and reading of lists of strings to a path specified at construction.
*/
public class FileSupport {
   private String defaultPath;
   
   //Stores and creates the required directory if it doesn't already exist.
   public FileSupport(String defaultPath){
	   this.defaultPath =  defaultPath;
	   
	   File dir = new File(defaultPath);
	   
	   if(!dir.exists()){
		   try{
			   dir.mkdir();
		   } catch(SecurityException e){
			   
		   }
		   
	   }
   }
   
   //Dumps all of lines into defaultPath\fileName
   public Boolean saveToFile(String fileName, Object lines) {
	   if(lines == null || fileName == null){
		   return false;
	   }
	   try{
		   ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream((defaultPath+"\\" + fileName)));
		   objOut.writeObject(lines);
		   return true;
	   } catch (IOException e){
		   
	   } 
	   return false;
   }
   
   //We won't be handling large files, so we can use simple IO.
   public Object loadFromFile(String fileName) {
	   Object obj = null;
	   try{
		   FileInputStream FIS = new FileInputStream((defaultPath+"\\" + fileName));
		   ObjectInputStream onjIn = new ObjectInputStream(FIS);
		   obj = onjIn.readObject();
	   } catch (NoSuchFileException e){
		 saveToFile(fileName, null);  
	   } catch (IOException e) {
	   } catch (ClassNotFoundException e) {
	   } 
	   return obj;
   }
   
 
   
   
   //Uses the default path when its getting files
   public List<File> getListing(){
	   return getFileList(new File(defaultPath));
   }
   
   
   //Need to get a list of files from a directory
   private List<File> getFileList(File root){
	   List<File> returnable = new ArrayList<File>();
	   if(root.exists()){
		   for(File currentSubFile : root.listFiles()){
			   if(currentSubFile.isDirectory()){
				   returnable.addAll(getFileList(currentSubFile));
			   } else {
				   returnable.add(currentSubFile);
			   }
		   }
	   }
	   return returnable;
   }

   
   }
