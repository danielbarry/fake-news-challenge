package netizens.fnc;

import java.util.HashMap;

/**
 * MarkerNumWordsMatch.java
 *
 * Use the Marker class to calculate the number of words that match in the head
 * and body.
 **/
public class MarkerNumWordsMatch implements Marker{
  private static final String name = "#num_words_match";

  public String getName(){
    return name;
  }

  public double analyse(DataSet ds, int y){
    Database holder = ds.getDatabase();
    return matchingWords(
      holder.getString(DataSet.BODY_LOCATION, y),
      holder.getString(DataSet.HEAD_LOCATION, y)
    );
  }

  /**
   * matchingWords()
   *
   * Returns the number of matching words between two Strings.
   *
   * @param a The first String.
   * @param b The second String.
   * @return The number of shared words between two Strings.
   **/
  private static double matchingWords(String a, String b){
    /* Store the words from String a */
    HashMap<String, Integer> lookup = generateDictionary(a);
    /* Count the number of matching words */
    String[] words = splitWords(b);
    int count = 0;
    for(int x = 0; x < words.length; x++){
      if(lookup.get(words[x]) != null){
        count++;
      }
    }
    return count;
  }

  /**
   * generateDictionary()
   *
   * Generates a dictionary for the words in a String, containing the frequency
   * in which they appear.
   *
   * @param str The String to analyse.
   * @return A HashMap containing the String as a key and the frequency as an
   * object.
   **/
  private static HashMap<String, Integer> generateDictionary(String str){
    HashMap<String, Integer> dict = new HashMap<String, Integer>();
    String[] words = splitWords(str);
    for(int x = 0; x < words.length; x++){
      if(dict.get(words[x]) == null){
        dict.put(words[x], 1);
      }else{
        dict.put(words[x], dict.get(words[x]) + 1);
      }
    }
    return dict;
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
