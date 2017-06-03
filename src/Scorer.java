package netizens.fnc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Scorer {

  String scorerPath;
  ArrayList< String > output = new ArrayList<>();

  public Scorer()
  {
    scorerPath = "scorer.py";
  }

  public Scorer( String scorerPath )
  {
    this.scorerPath = scorerPath;
  }

  public void run()
  {
    if( new File( scorerPath ).exists() == false )
      Main.error( "Scorer: invalid path: " + scorerPath );

    // TODO: Make passed databases as modifiable args.
    String[] scorerArgs = new String[]{ "python", scorerPath, "train_stances_fix.csv", "results_fix.csv" };
    Process scorer = null;

    /* Launch the scorer process through python */
    try{
      scorer = Runtime.getRuntime().exec( scorerArgs );
    } catch( Exception ex )
    {
      ex.printStackTrace();
      Main.error( "Scorer exception: " + ex );
    }

    /* Get output from the python */
    BufferedReader reader = new BufferedReader( new InputStreamReader( scorer.getInputStream() ) );
    String line;

    try{

      while( ( line = reader.readLine() ) != null )
        output.add( line );

    } catch ( Exception ex )
    {
      ex.printStackTrace();
      Main.error( "Scorer reader exception: " + ex );
    }

  }

  public double getScore()
  {
    for ( String s : output ) {
      if( s.contains( "ACCURACY" ) )
      {
        String score;

        try{
          score = s.substring( s.length() - 5, s.length() );

          /* Ignore */
        } catch ( Exception ex )
        {
          continue;
        }

        try{
          return Double.parseDouble( score );
        } catch( Exception ex )
        {
          ex.printStackTrace();
          Main.error( "Scorer: getScore exception: " + ex );
        }
      }
    }

    /* Not found, something is wrong */
    Main.error( "Scorer: getScore result not found!" );
    return -1;
  }

}
