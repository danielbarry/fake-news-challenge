package netizens.fnc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Database.java
 *
 * This class handles the arbitrary database as set by the Fake News Challenge,
 * allowing the data to be loaded and saved. The data in this class can be of
 * any type, as long as it is of type CSV. The user of this class is
 * responsible for knowing the type of data that is stored.
 **/
public class Database{
  private File file;
  private ArrayList<Object> head;
  private ArrayList<ArrayList<Object>> data;

  /**
   * Database()
   *
   * Load the database from file if it exists, into memory.
   *
   * @param filename The filename to be used for loading and saving of the
   * database.
   **/
  public Database(String filename){
    file = new File(filename);
    /* Load header line */
    ArrayList<Object> raw = loadFile(file);
    if(raw == null){
      Main.error("failed to load file");
    }
    /* Validate header */
    int vh = -1;
    if(raw.size() >= 1){
      vh = validHeader((String)(raw.get(0)));
    }
    if(vh < 0){
      Main.error("invalid header");
    }
    /* Store header */
    head = validEntry((String)(raw.get(0)));
    raw.remove(0);
    /* Load and validate entries */
    data = new ArrayList<ArrayList<Object>>();
    for(int x = 0; x < raw.size(); x++){
      ArrayList<Object> e = validEntry((String)(raw.get(x)));
      if(e != null){
        data.add(e);
      }else{
        Main.error("invalid data entry");
      }
    }
  }

  /**
   * Database()
   *
   * Create a new database in RAM with the specified structure.
   *
   * @param filename The filename to be used for loading and saving of the
   * database.
   * @param header A String containing the first CSV row, labelling the data
   * individually.
   **/
  public Database(String filename, String header){
    file = new File(filename);
    /* Check the header */
    if(validHeader(header) >= 0){
      head = validEntry(header);
      data = new ArrayList<ArrayList<Object>>();
    }else{
      Main.error("invalid header");
    }
  }

  /**
   * validHeader()
   *
   * Validates whether the header is correct in form, returning the number of
   * separate entities.
   *
   * @param header The header String to be tested.
   * @return The number of headers in the header String, otherwise -1 if there
   * is a fault.
   **/
  private int validHeader(String header){
    /* Check whether header is not NULL */
    if(header == null){
      return -1;
    }
    /* Extract headers */
    String[] headers = header.split(",");
    for(int x = 0; x < headers.length; x++){
      headers[x] = headers[x].trim();
    }
    /* Check if header contains entities */
    if(headers.length < 1){
      return -1;
    }
    /* Check each header has a label */
    for(int x = 0; x < headers.length; x++){
      if(headers[x].length() <= 0){
        return -1;
      }
    }
    /* TODO: Allow uniqueness check to be switched. */
    ///* Check that each header is unique */
    //HashMap<String, String> ids = new HashMap<String, String>();
    //for(int x = 0; x < headers.length; x++){
    //  if(ids.containsKey(headers[x])){
    //    return -1;
    //  }else{
    //    ids.put(headers[x], headers[x]);
    //  }
    //}
    /* Return the number of headers */
    return headers.length;
  }

  /**
   * validEntry()
   *
   * Validates whether a entry row is correct in form, returning an array of
   * the formatted String data.
   *
   * @param entry The entry String to be tested.
   * @return The correctly formatted String array, otherwise NULL.
   **/
  private ArrayList<Object> validEntry(String entry){
    /* Check whether entry is not NULL */
    if(entry == null){
      return null;
    }
    /* Extract values */
    String[] entries = entry.split(",");
    for(int x = 0; x < entries.length; x++){
      entries[x] = entries[x].trim();
    }
    /* Check if entry contains entities */
    if(entries.length < 1){
      return null;
    }
    /* Check each entry has a value */
    for(int x = 0; x < entries.length; x++){
      if(entries[x].length() <= 0){
        return null;
      }
    }
    /* Return the number of headers */
    ArrayList<Object> e = new ArrayList<Object>();
    for(int x = 0; x < entries.length; x++){
      e.add(entries[x]);
    }
    return e;
  }

  /**
   * getWidth()
   *
   * Get the width of our data (columns).
   *
   * @return The width of the data.
   **/
  public int getWidth(){
    return head.size();
  }

  /**
   * getHeight()
   *
   * Get the height of our data (rows).
   *
   * @return The height of the data.
   **/
  public int getHeight(){
    return data.size();
  }

  /**
   * getData()
   *
   * Get data array for direct access to it.
   * @return Data array
   */
  public ArrayList< ArrayList<Object> > getData()
  {
    return data;
  }

  /**
   * getString()
   *
   * Get the data String at a given column and row.
   *
   * @param col The column to retrieve the data from.
   * @param row The row to retrieve the data from.
   * @return Get the String at the given location, NULL if data doesn't exist.
   **/
  public String getString(int col, int row){
    if(row < 0 || row >= data.size()){
      return null;
    }else{
      if(col < 0 || col >= data.get(row).size()){
        return null;
      }else{
        return (String)(data.get(row).get(col));
      }
    }
  }

  /**
   * getDouble()
   *
   * Get the data Double at a given column and row.
   *
   * @param col The column to retrieve the data from.
   * @param row The row to retrieve the data from.
   * @return Get the Double at the given location, NULL if data doesn't exist.
   **/
  public Double getDouble(int col, int row){
    if(row < 0 || row >= data.size()){
      return null;
    }else{
      if(col < 0 || col >= data.get(row).size()){
        return null;
      }else{
        return (Double)(data.get(row).get(col));
      }
    }
  }

  /**
   * setData()
   *
   * Set the data String at a given column and row.
   *
   * @param col The column to set the data.
   * @param row The row to set the data.
   * @param elem The element to set into the dataset.
   **/
  public void setData(int col, int row, Object elem){
    if(row < 0 || row >= data.size()){
      /* Check whether we are inserting a new record */
      if(row == data.size()){
        data.add(new ArrayList<Object>());
      }else{
        Main.error("row is outside dataset");
      }
    }
    if(col < 0 || col >= data.get(row).size()){
      /* Check whether we are inserting a new record */
      if(col == data.get(row).size()){
        data.get(row).add("");
      }else{
        Main.error("col is outside dataset");
      }
    }
    data.get(row).set(col, elem);
  }

  /**
   * getHead()
   *
   * Get the head String at a given column.
   *
   * @param col The column to retrieve the data from.
   * @return Get the String at the given location, NULL if data doesn't exist.
   **/
  public String getHead(int col){
    if(col < 0 || col >= head.size()){
      return null;
    }else{
      return (String)(head.get(col));
    }
  }

  /**
   * setHead()
   *
   * Set the head String at a given column.
   *
   * @param col The column to set the data.
   * @param elem The element to set into the dataset.
   **/
  public void setHead(int col, String elem){
    if(col < 0 || col >= head.size()){
      Main.error("col is outside dataset");
    }else{
      head.set(col, elem);
    }
  }

  /**
   * saveDatabase()
   *
   * Saves the current database to disk.
   **/
  public void saveDatabase(){
    saveFile(file, head, data);
  }

  /**
   * saveDatabase()
   *
   * @param newPath - the new path the database is going to saved as.
   */
  public void saveDatabase( String newPath ) { saveFile( new File( newPath ), head, data ); }

  /**
   * loadFile()
   *
   * Load the file from the disk into a String array, split by the lines
   * loaded.
   *
   * @param file The file to be loaded from disk.
   * @return An Object array of text lines contained in the file, NULL on a
   * failure.
   **/
  private static ArrayList<Object> loadFile(File file){
    ArrayList<Object> data = new ArrayList<Object>();
    try{
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);
      /* Read file line by line */
      String line = br.readLine();
      while(line != null){
        data.add(line);
        line = br.readLine();
      }
      /* Close file */
      br.close();
      fr.close();
    }catch(IOException e){
      return null;
    }
    return data;
  }

  /**
   * saveFile()
   *
   * Save the file to disk, in a CSV format.
   *
   * @param file The file to be saved to disk.
   * @param head The header data.
   * @param data The rows data.
   **/
  private static void saveFile(
    File file,
    ArrayList<Object> head,
    ArrayList<ArrayList<Object>> data
  ){
    try{
      FileWriter fw = new FileWriter(file);
      BufferedWriter bw = new BufferedWriter(fw);
      /* Write header */
      for(int x = 0; x < head.size(); x++){
        bw.write((x == 0 ? "" : ",") + head.get(x));
      }
      /* Write data */
      for(int y = 0; y < data.size(); y++){
        bw.write("\n");
        for(int x = 0; x < data.get(y).size(); x++){
          bw.write((x == 0 ? "" : ",") + data.get(y).get(x));
        }
      }
      /* Close file */
      bw.close();
      fw.close();
    }catch(IOException e){
      Main.error("failed to save file");
    }
  }
}
