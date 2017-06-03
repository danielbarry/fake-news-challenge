package netizens.fnc;

/**
 * Main.java
 *
 * The main entry class for the program, responsible for testing the arguments
 * passed.
 **/
public class Main{
  /**
   * MODE.Main.java
   *
   * An abstract mode.
   **/
  private enum MODE{
    KNEAR
  }

  private Database titles;
  private Database bodies;
  private Database uTitles;
  private Database uBodies;
  private String   fixPath = null;
  private boolean  score = false;

  private MODE mode;
  private int jobs;
  private int kner;
  private int runs;

  /**
   * main()
   *
   * Creates an instance of Main and passes the arguments to it.
   *
   * @param args The arguments passed to the program.
   **/
  public static void main(String[] args){
    new Main(args);
  }

  /**
   * Main()
   *
   * This method is responsible for parsing the arguments supplied to this
   * program and executing as dictated by their meaning.
   *
   * @param args The arguments passed to the program.
   **/
  public Main(String[] args){
    /* Set variables */
    titles = null;
    bodies = null;
    uTitles = null;
    uBodies = null;
    mode = MODE.KNEAR;
    jobs = 1;
    kner = 1;
    runs = 1;

    /* Iterate over the arguments */
    for(int x = 0; x < args.length; x++){
      /* Check the input parameters */
      switch(args[x]){
        case "-c" :
        case "--clss" :
          x = clss(args, x);
          break;
        case "-h" :
        case "--help" :
          x = help(args, x);
          break;
        case "-j" :
        case "--job" :
          x = jobs(args, x);
          break;
        case "-k" :
        case "--kner" :
          x = kner(args, x);
          break;
        case "-m" :
        case "--mode" :
          x = mode(args, x);
          break;
        case "-r" :
        case "--run" :
          x = runs(args, x);
          break;
        case "-u" :
        case "--ucls" :
          x = ucls(args, x);
          break;
        case "-f":
        case "--fix":
          x = fix(args, x);
          break;
        case "-s":
        case "--score":
          // TODO: Currently accepts no arguments, because we got a lot already
          x = score(args,x);
          break;
      }
    }
    /* Select required algorithm */
    Classifier[] classifier = new Classifier[jobs];
    for(int x = 0; x < jobs; x++){
      switch(mode){
        case KNEAR :
          classifier[x] = new KNear(x, jobs, kner);
          break;
        default :
          error("mode doesn't exist");
          break;
      }
    }
    /* Make sure the user wants us to classify */
    if(titles != null && bodies != null && uTitles != null && uBodies != null){
      /* Build the datasets */
      DataSet trainData = new DataSet(titles, bodies);
      DataSet testData = new DataSet(uTitles, uBodies);
      /* Record timestamps */
      long start = System.currentTimeMillis();
      /* Analyse the datasets */
      System.out.println("Analysing...");
      trainData.analyse();
      testData.analyse();
      System.out.println("Analyse:" + (System.currentTimeMillis() - start) + "ms");
      start = System.currentTimeMillis();
      /* Train data */
      for(int x = 0; x < jobs; x++){
        System.out.println("Training[" + x + "]...");
        classifier[x].train(runs, trainData);
        ((Thread)classifier[x]).start();
      }
      /* Wait for training to complete */
      for(int x = 0; x < jobs; x++){
        try{
          ((Thread)classifier[x]).join();
        }catch(InterruptedException e){
          /* Do nothing */
        }
      }
      System.out.println("Train:" + (System.currentTimeMillis() - start) + "ms");
      start = System.currentTimeMillis();
      /* Test data */
      for(int x = 0; x < jobs; x++){
        System.out.println("Labelling[" + x + "]...");
        classifier[x].test(testData);
        ((Thread)classifier[x]).start();
      }
      /* Wait for training to complete */
      for(int x = 0; x < jobs; x++){
        try{
          ((Thread)classifier[x]).join();
        }catch(InterruptedException e){
          /* Do nothing */
        }
      }
      System.out.println("Test:" + (System.currentTimeMillis() - start) + "ms");
      /* Save test data */
      System.out.println("Saving...");
      titles.saveDatabase();
      /* Save the results data */
      testData.getTitleDatabase("results.csv").saveDatabase();
    }

    /* Fix the analyzed database, so it matches the original one */
    if( fixPath != null )
    {
      Database fixClassified    = new Database( "results.csv" );

      /* OriginalTitles are the whole original dataset which contains all
         of the titles.
       */
      Database originalTitles   = new Database( fixPath );

      Fix fix = new Fix( fixClassified, originalTitles );
      fix.fix();

      /* Save both aligned/fixed databases */
      fix.getFixedDatabase().saveDatabase( "results_fix.csv" );
      fix.getFixedOriginals().saveDatabase( "train_stances_fix.csv" );

      if( score )
      {
        Scorer scorer = new Scorer();
        scorer.run();

        System.out.println( "Score: " + scorer.getScore() );
      }
    } else {
      if( score )
        System.out.println( "-s argument must be run together with -f argument" );
    }

  }

  /**
   * clss()
   *
   * Load a file containing the classified data.
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
  private int clss(String[] args, int x){
    x += 2;
    if(x >= args.length){
      error("clss parameter not given");
    }else{
      titles = new Database(args[x - 1]);
      bodies = new Database(args[x]);
    }
    return x;
  }

  /**
   * help()
   *
   * Displays the help for this program.
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
  private int help(String[] args, int x){
    System.out.println(
      "\n./fnc.jar [OPT]" +
      "\n" +
      "\n  OPTions" +
      "\n" +
      "\n    -c  --clss    Classified data" +
      "\n      <FILE>   CSV titles file" +
      "\n      <FILE>   CSV bodies file" +
      "\n    -h  --help    Displays this help" +
      "\n    -k  --kner    Set the K for k-nearest" +
      "\n      <INT>    Number of nearest" +
      "\n    -j  --jobs    Set the number of jobs to run" +
      "\n      <INT>    Number of jobs" +
      "\n    -m  --mode    Set the mode" +
      "\n      knear    (def) k-nearest neighbours" +
      "\n    -r  --runs    Set the number of runs" +
      "\n      <INT>    Number of runs" +
      "\n    -u  --ucls    Unclassified data" +
      "\n      <FILE>   CSV titles file" +
      "\n      <FILE>   CSV bodies file" +
      "\n    -f  --fix     Fixes the results to it's original form." +
      "\n      <FILE>      The original whole titles database" +
      "\n"
    );
    return x;
  }

  /**
   * jobs()
   *
   * Set the number of jobs to train the classifier with.
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
  private int jobs(String[] args, int x){
    ++x;
    if(x >= args.length){
      error("runs parameter not given");
    }else{
      try{
        jobs = Integer.parseInt(args[x]);
      }catch(NumberFormatException e){
        error("runs parameter invalid");
      }
    }
    return x;
  }

  /**
   * kner()
   *
   * Set the k in the k-nearest values.
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
  private int kner(String[] args, int x){
    ++x;
    if(x >= args.length){
      error("kner parameter not given");
    }else{
      try{
        kner = Integer.parseInt(args[x]);
      }catch(NumberFormatException e){
        error("kner parameter invalid");
      }
    }
    return x;
  }

  /**
   * mode()
   *
   * Sets the mode for the program.
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
  private int mode(String[] args, int x){
    ++x;
    if(x >= args.length){
      error("mode parameter not given");
    }else{
      switch(args[x]){
        case "knear" :
          mode = MODE.KNEAR;
          break;
        default :
          error("`" + args[x] + "` not a mode");
          break;
      }
    }
    return x;
  }

  /**
   * runs()
   *
   * Set the number of runs to train the classifier for.
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
  private int runs(String[] args, int x){
    ++x;
    if(x >= args.length){
      error("runs parameter not given");
    }else{
      try{
        runs = Integer.parseInt(args[x]);
      }catch(NumberFormatException e){
        error("runs parameter invalid");
      }
    }
    return x;
  }

  /**
   * ucls()
   *
   * Load a file containing the unclassified data.
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
  private int ucls(String[] args, int x){
    x += 2;
    if(x >= args.length){
      error("ucls parameter not given");
    }else{
      uTitles = new Database(args[x - 1]);
      uBodies = new Database(args[x]);
    }
    return x;
  }

  /**
   * fix()
   *
   * Load a file containing classified data that needs to be fixed.
   * Fixed meaning it's titles will be converted to the original titles
   *
   * WARNING: This function depends on the original classified data
   * which it expects that it will provided (titles database)
   *
   * @param args The arguments passed to the program.
   * @param x The offset in the array for the case that triggered this method
   * call.
   * @return The position of the last argument used.
   **/
    private int fix( String[] args, int x )
    {
        ++x;

        if( x >= args.length )
        {
            error( "fix parameter not given" );
        } else {
            fixPath = args[x];
        }

        return x;
    }

    private int score( String[] args, int x )
    {
      x++;

      /* Accepts no args for now to make it easier */
      score = true;

      return x;
    }


  /**
   * error()
   *
   * Displays an error message and hard quits the program.
   *
   * @param msg The message to be displayed.
   **/
  public static void error(String msg){
    System.err.println("ERR::[" + msg + "]");
    System.exit(0);
  }
}
