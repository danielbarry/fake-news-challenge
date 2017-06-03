package netizens.fnc;

import java.util.HashMap;

/**
 * MarkerEntropyBody.java
 *
 * Use the Marker class to calculate the entropy in the body.
 **/
public class MarkerEntropyBody implements Marker{
  private static final String name = "#entropy_body";

  public String getName(){
    return name;
  }

  public double analyse(DataSet ds, int y){
    Database holder = ds.getDatabase();
    return strEntropy(holder.getString(DataSet.BODY_LOCATION, y));
  }

  /**
   * strEntropy()
   *
   * Calculate the entropy for a String.
   *
   * @param str The String to calculate the entropy for.
   * @return The entropy of the String, stored as a String.
   **/
  private static double strEntropy(String str){
    HashMap<String, Integer> dict = generateDictionary(str);
    double size = splitWords(str).length;
    /* Generate entropy set */
    double[] set = new double[dict.size()];
    for(int x = 0; x < set.length; x++){
      set[x] = (double)((int)(dict.values().toArray()[x])) / size;
    }
    /* Calculate entropy */
    return calcEntropy(set);
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

  /**
   * calcEntropy()
   *
   * Gets the entropy of a given set of probabilities.
   *
   * @param set The normalised set of probabilities to be checked.
   * @return The entropy of the given set.
   **/
  private static double calcEntropy(double[] set){
    /* Sum all the entropy probabilities */
    double entropy = 0;
    for(int x = 0; x < set.length; x++){
      /* Make sure we're not calculating a zero case */
      if(set[x] > 0){
        /* Calculate entropy */
        entropy += set[x] * (Math.log(set[x]) / Math.log(2));
      }
    }
    return -entropy;
  }
}
