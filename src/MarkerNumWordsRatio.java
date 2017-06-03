package netizens.fnc;

/**
 * MarkerNumWordsRatio.java
 *
 * Use the Marker class to calculate the ratio of the number of words in the
 * head and the number of words in the body.
 **/
public class MarkerNumWordsRatio implements Marker{
  private static final String name = "#num_words_ratio";

  public String getName(){
    return name;
  }

  public double analyse(DataSet ds, int y){
    Database holder = ds.getDatabase();
    return ratio(
      holder.getString(DataSet.BODY_LOCATION, y),
      holder.getString(DataSet.HEAD_LOCATION, y)
    );
  }

  /**
   * ratio()
   *
   * The ratio of words between two Strings.
   *
   * @param a The first String.
   * @param b The second String.
   * @return The ratio between the two numbers as a String.
   **/
  private static double ratio(String a, String b){
    double x = wordCount(a);
    double y = wordCount(b);
    return x / y;
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
