package netizens.fnc;

/**
 * MarkerNumWordsBody.java
 *
 * Use the Marker class to calculate the number of words in the words.
 **/
public class MarkerNumWordsBody implements Marker{
  private static final String name = "#num_words_body";

  public String getName(){
    return name;
  }

  public double analyse(DataSet ds, int y){
    Database holder = ds.getDatabase();
    return wordCount(holder.getString(DataSet.BODY_LOCATION, y));
  }

  /**
   * wordCount()
   *
   * Gets the word count for a String containing words.
   *
   * @param str The String to be searched for words.
   * @return The word count converted to a String.
   **/
  private static double wordCount(String str){
    return splitWords(str).length;
  }

  /**
   * splitWords()
   *
   * Split a String into the words it comprises of.
   *
   * @param str The String to be split.
   * @return The split String into words.
   **/
  private static String[] splitWords(String str){
    return str.split(" ");
  }
}
