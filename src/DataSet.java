package netizens.fnc;

/**
 * DataSet.java
 *
 * The dataset representing the challenge data for either the training,
 * validation or unlabelled data.
 **/
public class DataSet{
  public static final int HEAD_LOCATION = 0;
  public static final int BODY_LOCATION = 4;

  private static final Marker[] MARKERS = new Marker[]{
    new MarkerNumWordsHead(),
    new MarkerNumWordsBody(),
    new MarkerNumWordsRatio(),
    new MarkerNumWordsMatch(),
    new MarkerEntropyHead(),
    new MarkerEntropyBody()//,
    //new MarkerMatchDictHead(),
    //new MarkerMatchDictBody()
  };

  private Database holder;

  /**
   * DataSet()
   *
   * The DataSet is comprised of the header files and body files.
   *
   * @param titles The titles in the dataset.
   * @param bodies The bodies in the dataset.
   **/
  public DataSet(Database titles, Database bodies){
    /* Create holder database */
    String header = "";
    for(int x = 0; x < titles.getWidth(); x++){
      header += (x == 0 ? "" : ",") + titles.getHead(x);
    }
    for(int x = 0; x < bodies.getWidth(); x++){
      header += "," + bodies.getHead(x);
    }
    for(int x = 0; x < MARKERS.length; x++){
      header += "," + MARKERS[x].getName();
    }
    holder = new Database("holder.csv", header);
    /* Copy title data */
    for(int y = 0; y < titles.getHeight(); y++){
      for(int x = 0; x < titles.getWidth(); x++){
        holder.setData(x, y, titles.getString(x, y));
      }
    }
    /* Copy body data */
    for(int y = 0; y < titles.getHeight(); y++){
      /* Search data */
      int z = 0;
      for(; z < bodies.getHeight(); z++){
        if(holder.getString(1, y).equals(bodies.getString(0, z))){
          break;
        }
      }
      /* Make sure an error didn't occur */
      if(z >= bodies.getHeight()){
        Main.error("body id `" + holder.getString(1, y) + "` not found");
      }
      /* Copy data */
      for(int x = 0; x < bodies.getWidth(); x++){
        holder.setData(titles.getWidth() + x, y, bodies.getString(x, z));
      }
    }
  }

  /**
   * analyse()
   *
   * Analyses this DataSet and stores the result in a new internal database.
   **/
  public void analyse(){
    /* Iterate over each element */
    for(int y = 0; y < holder.getHeight(); y++){
      /* Sanitize head String */
      holder.setData(
        HEAD_LOCATION, y,
        sanitizeStr(holder.getString(HEAD_LOCATION, y))
      );
      /* Sanitize body String */
      holder.setData(BODY_LOCATION, y,
        sanitizeStr(holder.getString(BODY_LOCATION, y))
      );
      /* Analyse the various values */
      for(int i = 0; i < MARKERS.length; i++){
        holder.setData(
          holder.getWidth() - MARKERS.length + i, y,
          MARKERS[i].analyse(this, y)
        );
      }
    }
    /* Normalise the data */
    normalise();
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

  /**
   * normalise()
   *
   * Normalises the analysis data.
   **/
  private void normalise(){
    double[] min = new double[MARKERS.length];
    double[] max = new double[MARKERS.length];
    double[] nrm = new double[MARKERS.length];
    /* Learn the min and max values */
    for(int y = 0; y < holder.getHeight(); y++){
      /* Iterate through analysis columns */
      for(int x = holder.getWidth() - MARKERS.length; x < holder.getWidth(); x++){
        int z = x - (holder.getWidth() - MARKERS.length);
        double temp = holder.getDouble(x, y);
        /* Check min case */
        if(y == 0 || temp < min[z]){
          min[z] = temp;
        }
        /* Check max case */
        if(y == 0 || temp > max[z]){
          max[z] = temp;
        }
      }
    }
    /* Calculate normalisation */
    for(int y = 0; y < MARKERS.length; y++){
      nrm[y] = 1 / (max[y] - min[y]);
    }
    /* Apply normalisation */
    for(int y = 0; y < holder.getHeight(); y++){
      /* Iterate through analysis columns */
      for(int x = holder.getWidth() - MARKERS.length; x < holder.getWidth(); x++){
        int z = x - (holder.getWidth() - MARKERS.length);
        double temp = holder.getDouble(x, y);
        holder.setData(x, y, (temp - min[z]) * nrm[z]);
      }
    }
  }

  /**
   * getDatabase()
   *
   * Gets the Database supporting this DataSet.
   *
   * Gets the compiled Database.
   **/
  public Database getDatabase(){
    return holder;
  }

  /**
   * getAnalysisWidth()
   *
   * Get the width of the analysis parameters.
   *
   * @return The width of the analysis parameters.
   **/
  public static int getAnalysisWidth(){
    return MARKERS.length;
  }

  /**
   * getTitleDatabase()
   *
   * Get the current title database from the built database and return this.
   *
   * @param filename The filename to save.
   * @return The title data in the form of a database.
   **/
  public Database getTitleDatabase(String filename){
    String head =
      holder.getHead(0) + "," +
      holder.getHead(1) + "," +
      holder.getHead(2);
    Database db = new Database(filename, head);
    for(int y = 0; y < holder.getHeight(); y++){
      for(int x = 0; x < 3; x++){
        db.setData(x, y, holder.getString(x, y));
      }
    }
    return db;
  }
}
