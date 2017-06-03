package netizens.fnc;

/**
 * MarkerMatchDictHead.java
 *
 * Words from the top 100 most common English words, with quadratic back off.
 **/
public class MarkerMatchDictHead implements Marker{
  private static final String name = "#num_dict_head";
  private static final String[] dict = new String[]{
    "the",
    "be",
    "to",
    "of",
    "and",
    "a",
    "in",
    "that",
    "have",
    "i",
    "it",
    "for",
    "not",
    "on",
    "with",
    "he",
    "as",
    "you",
    "do",
    "at",
    "this",
    "but",
    "his",
    "by",
    "from",
    "they",
    "we",
    "say",
    "her",
    "she",
    "or",
    "an",
    "will",
    "my",
    "one",
    "all",
    "would",
    "there",
    "their",
    "what",
    "so",
    "up",
    "out",
    "if",
    "about",
    "who",
    "get",
    "which",
    "go",
    "me",
    "when",
    "make",
    "can",
    "like",
    "time",
    "no",
    "just",
    "him",
    "know",
    "take",
    "person",
    "into",
    "year",
    "your",
    "good",
    "some",
    "could",
    "them",
    "see",
    "other",
    "than",
    "then",
    "now",
    "look",
    "only",
    "come",
    "its",
    "over",
    "think",
    "also",
    "back",
    "after",
    "use",
    "two",
    "how",
    "our",
    "work",
    "first",
    "well",
    "way",
    "even",
    "new",
    "want",
    "because",
    "any",
    "these",
    "give",
    "day",
    "most",
    "us"
  };

  public String getName(){
    return name;
  }

  public double analyse(DataSet ds, int y){
    Database holder = ds.getDatabase();
    return dictCount(holder.getString(DataSet.BODY_LOCATION, y));
  }

  /**
   * dictCount()
   *
   * Counts the number of matching words in the two lists with quadratic back
   * off.
   *
   * @param str The String to be searched for words.
   * @return The word count converted to a String.
   **/
  private static double dictCount(String str){
    /* Split the words up */
    String[] words = splitWords(str);
    double sum = 0;
    /* Iterate over the words to be tested */
    for(int x = 0; x < words.length; x++){
      for(int i = 0; i < dict.length; i++){
        /* Check for matches */
        if(words[x].equals(dict[i])){
          /* Sum our value */
          sum += 1.0 / (i * i);
          /* Stop searching inner loop if we have a match */
          break;
        }
      }
    }
    /* Return the normalised value on the number of words */
    return sum / words.length;
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

