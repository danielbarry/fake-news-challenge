package netizens.fnc;

public class Fix
{
  private Database classifiers      = null;
  private Database originals        = null;

  public Fix( Database classifiers, Database originals )
  {
    if( classifiers == null || originals == null )
      Main.error( "Fix: null database" );

    this.classifiers  = classifiers;
    this.originals    = originals;

  }

  public Database getFixedDatabase()
  {
    return classifiers;
  }

  public Database getFixedOriginals()
  {
    return originals;
  }

  public void fix()
  {
    int beginning     = findBeginning();
    System.out.println( "Fix: Closest index: " + beginning );

    /* This might imply that the originals database is cut and doesn't contain
       all titles.
     */
    if( beginning == -1 )
      Main.error( "Fix.java - couldn't find the beginning" );

    /* Replace the titles */
    fixDatabase( beginning );
    fixOriginals( beginning );
  }

  private int findBeginning() {
    int closestIndex = -1;

    /* Get the first title and get words in it */
    String baseTitle  = sanitizeStr( classifiers.getString( 0, 0 ) );
    int baseId        = Integer.parseInt( classifiers.getString( 1, 0 ) );

    /* Iterate the original database to try and find the beginning
       for the classifier database.
     */
    for ( int i = 0; i < originals.getHeight(); i++ ) {
      String currentBody = originals.getString( 1, i );

      /* The body ID's should match */
      if ( Integer.parseInt( currentBody ) == baseId ) {
        String currentStr = sanitizeStr( originals.getString( 0, i ) );

        /* Found the title within the original titles */
        if( currentStr.equalsIgnoreCase( baseTitle ) )
        {
          closestIndex = i;
          break;
        }
      }
    }

    return closestIndex;
  }

  private void fixDatabase( int startIndex )
  {
    int counter = 0;

    for( int i = startIndex; i < originals.getHeight(); i++ )
    {
      /* Replace the titles with the proper ones. */
      classifiers.setData( 0, counter, originals.getString( 0, i ) );
      counter++;
    }
  }

  private void fixOriginals( int startIndex )
  {
    /* Could be improved much more by getting sublist of the data
       and replacing it in database, but I'm too lazy to add the function in Database.java
     */

    for( int i = 0; i < startIndex; i++ )
    {
      /* Remove first element until we reach the one we are looking for */
      originals.getData().remove( 0 );

    }

  }

  /**
   * sanitizeStr()
   *
   * Sanitize the data, removing special characters and extra spaces. The data
   * is also converted to lowercase for further analysis.
   *
   * @param str The String to sanitize.
   * @return The sanitized String.
   **/
  private String sanitizeStr(String str){
    /* Convert String to lowercase */
    str = str.toLowerCase();
    /* Remove word splitting */
    str = str.replace("'", "");
    /* Search characters, convert anything not letters or numbers */
    byte[] s = str.getBytes();
    for(int x = 0; x < s.length; x++){
      if((s[x] < 'a' || s[x] > 'z') && (s[x] < '0' || s[x] > '9')){
        s[x] = ' ';
      }
    }
    str = new String(s);
    /* Compact sequences of spaces for different lengths */
    str = str.replace("          ", " ");
    str = str.replace("         ", " ");
    str = str.replace("        ", " ");
    str = str.replace("       ", " ");
    str = str.replace("      ", " ");
    str = str.replace("     ", " ");
    str = str.replace("    ", " ");
    str = str.replace("   ", " ");
    str = str.replace("  ", " ");
    /* Remove surrounding white space */
    str = str.trim();
    /* Compact String */
    return str;
  }
}
